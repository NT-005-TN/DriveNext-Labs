package ru.mtuci.drivenext.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.mtuci.drivenext.BuildConfig
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.cars.Car
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
import ru.mtuci.drivenext.presentation.common.FeatureStubActivity
import ru.mtuci.drivenext.presentation.catalog.*
import ru.mtuci.drivenext.presentation.booking.BookingActivity
import ru.mtuci.drivenext.presentation.settings.SettingsActivity

open class MainActivity : WorkspaceActivity() {
    protected lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CarAdapter
    protected open val searchMode = false
    protected open val favoritesMode = false
    private val cars = mutableListOf<Car>()
    private var nextPage = 0
    private var hasMore = true
    private var currentQuery = ""
    private var searchJob: Job? = null
    // Номер applicationId ограничивает функции текущей лабораторной.
    private val labNumber by lazy { BuildConfig.APPLICATION_ID.substringAfterLast("lab").toIntOrNull() ?: 5 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Создаём Binding, адаптер и вертикальный RecyclerView.
        binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        adapter = CarAdapter(
            { open(CarDetailsActivity::class.java,it,4,"Карточка автомобиля") },
            { open(BookingActivity::class.java,it,4,"Бронирование автомобиля") })
        val layoutManager = LinearLayoutManager(this)
        binding.carList.layoutManager = layoutManager; binding.carList.adapter = adapter
        // Подгружаем следующую страницу возле конца списка.
        binding.carList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !favoritesMode && hasMore && !work.isBusy() &&
                    layoutManager.findLastVisibleItemPosition() >= adapter.itemCount - 2) loadNextPage()
            }
        })
        binding.progress.visibility = View.GONE
        if (searchMode || favoritesMode) binding.bottomNavigation.visibility = View.GONE
        if (searchMode || favoritesMode) {
            binding.backButton.visibility = View.VISIBLE
            binding.backButton.setOnClickListener { finish() }
        }
        if (favoritesMode) { binding.title.text="Избранное"; binding.searchView.visibility=View.GONE }
        if (searchMode) {
            binding.title.text="Результаты поиска"
            val saved = getSharedPreferences("search",MODE_PRIVATE).getString("query","").orEmpty()
            binding.searchView.setQuery(savedInstanceState?.getString("query") ?: intent.getStringExtra("query") ?: saved,false)
        } else savedInstanceState?.getString("query")?.let { binding.searchView.setQuery(it,false) }
        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            // Enter запускает поиск без задержки.
            override fun onQueryTextSubmit(query:String):Boolean {
                scheduleSearch(query, immediate = true)
                return true
            }
            // Обычный ввод использует debounce.
            override fun onQueryTextChange(query:String):Boolean {
                scheduleSearch(query)
                return true
            }
        })
        // Нижнее меню переключает каталог, избранное и настройки.
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navSettings -> startActivity(Intent(this,SettingsActivity::class.java))
                R.id.navFavorites -> if (labNumber < 4) showStub("Избранное",4)
                    else startActivity(Intent(this,FavoritesActivity::class.java))
                else -> load(reset = true)
            }; true
        }
    }
    // Перед загрузкой защищённых данных проверяем серверную сессию.
    override fun onStart() { super.onStart(); authModel.restore(force=true,protectedScreen=true) }
    override fun onAuthenticated() { load(binding.searchView.query.toString(), true) }
    // Отменяем устаревший Job и ждём паузу пользователя в наборе текста.
    private fun scheduleSearch(query:String, immediate:Boolean=false) {
        if (favoritesMode) return
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            if (!immediate) delay(SEARCH_DELAY_MS)
            load(query, true)
        }
    }
    // Новый запрос очищает старые страницы, продолжение сохраняет список.
    private fun load(query:String="", reset:Boolean=false) {
        if (searchMode) getSharedPreferences("search",MODE_PRIVATE).edit().putString("query",query).apply()
        if (favoritesMode) { work.run("favorites"); return }
        val normalized = query.trim()
        val newSearch = reset || normalized != currentQuery
        if (newSearch) {
            currentQuery = normalized; nextPage = 0; hasMore = true
            cars.clear(); adapter.submitList(emptyList())
        }
        loadNextPage(force = newSearch)
    }
    // Передаём во ViewModel текст, номер страницы и её размер.
    private fun loadNextPage(force:Boolean=false) {
        if (!hasMore || (!force && work.isBusy())) return
        work.run("cars", JSONObject().put("query",currentQuery).put("page",nextPage).put("page_size",PAGE_SIZE))
    }
    // Недоступные функции открываются как информационные заглушки.
    private fun open(screen:Class<*>,car:Car,availableFrom:Int,feature:String) {
        if (labNumber < availableFrom) showStub(feature,availableFrom)
        else startActivity(Intent(this,screen).putExtra("id",car.id))
    }
    private fun showStub(feature:String,availableFrom:Int) {
        startActivity(FeatureStubActivity.intent(this,feature,availableFrom))
    }
    override fun render(operation:String,value:JSONObject) {
        // Превращаем JSON-строки в Car и обновляем список без дубликатов.
        if(operation!="cars" && operation!="favorites") return
        val rows=value.getJSONArray("items")
        val received = (0 until rows.length()).map { Car.fromJson(rows.getJSONObject(it)) }
        if (operation == "cars") {
            val responsePage = value.optInt("page",0)
            if (responsePage == 0) cars.clear()
            cars.addAll(received.filterNot { candidate -> cars.any { it.id == candidate.id } })
            nextPage = responsePage + 1
            hasMore = value.optBoolean("has_more",false)
            adapter.submitList(cars.toList())
        } else adapter.submitList(received)
        binding.title.text=when { adapter.itemCount==0 -> "Ничего не найдено"; favoritesMode -> "Избранное"; currentQuery.isNotBlank() -> "Результаты поиска"; else -> "Доступные автомобили" }
    }
    override fun onSaveInstanceState(outState:Bundle) { outState.putString("query",binding.searchView.query.toString()); super.onSaveInstanceState(outState) }
    private companion object {
        const val PAGE_SIZE = 3
        const val SEARCH_DELAY_MS = 300L
    }
}
