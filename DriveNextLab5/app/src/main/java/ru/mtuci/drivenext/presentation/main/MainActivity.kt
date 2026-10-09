package ru.mtuci.drivenext.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.cars.Car
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
import ru.mtuci.drivenext.presentation.catalog.*
import ru.mtuci.drivenext.presentation.booking.BookingActivity
import ru.mtuci.drivenext.presentation.settings.SettingsActivity

open class MainActivity : WorkspaceActivity() {
    protected lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CarAdapter
    protected open val searchMode = false
    protected open val favoritesMode = false
    private var searchJob: kotlinx.coroutines.Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        adapter = CarAdapter({ open(CarDetailsActivity::class.java,it) },{ open(BookingActivity::class.java,it) })
        binding.carList.layoutManager = LinearLayoutManager(this); binding.carList.adapter = adapter
        binding.progress.visibility = View.GONE
        if (searchMode || favoritesMode) binding.bottomNavigation.visibility = View.GONE
        if (favoritesMode) { binding.title.text="Избранное"; binding.searchView.visibility=View.GONE }
        if (searchMode) {
            binding.title.text="Результаты поиска"
            val saved = getSharedPreferences("search",MODE_PRIVATE).getString("query","").orEmpty()
            binding.searchView.setQuery(savedInstanceState?.getString("query") ?: intent.getStringExtra("query") ?: saved,false)
        }
        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query:String):Boolean {
                if (searchMode) load(query) else startActivity(Intent(this@MainActivity,SearchResultsActivity::class.java).putExtra("query",query))
                return true
            }
            override fun onQueryTextChange(query:String):Boolean {
                if (searchMode) load(query)
                return true
            }
        })
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navSettings -> startActivity(Intent(this,SettingsActivity::class.java))
                R.id.navFavorites -> startActivity(Intent(this,FavoritesActivity::class.java))
                else -> load()
            }; true
        }
    }
    override fun onStart() { super.onStart(); authModel.restore(force=true,protectedScreen=true) }
    override fun onAuthenticated() { load(if(searchMode) binding.searchView.query.toString() else "") }
    private fun load(query:String="") {
        if (searchMode) getSharedPreferences("search",MODE_PRIVATE).edit().putString("query",query).apply()
        work.run(if(favoritesMode) "favorites" else "cars",JSONObject().put("query",query))
    }
    private fun open(screen:Class<*>,car:Car) { startActivity(Intent(this,screen).putExtra("id",car.id)) }
    override fun render(operation:String,value:JSONObject) {
        if(operation!="cars" && operation!="favorites") return
        val rows=value.getJSONArray("items")
        adapter.submitList((0 until rows.length()).map { Car.fromJson(rows.getJSONObject(it)) })
        binding.title.text=when { rows.length()==0 -> "Ничего не найдено"; favoritesMode -> "Избранное"; searchMode -> "Результаты поиска"; else -> "Доступные автомобили" }
    }
    override fun onSaveInstanceState(outState:Bundle) { outState.putString("query",binding.searchView.query.toString()); super.onSaveInstanceState(outState) }
}
