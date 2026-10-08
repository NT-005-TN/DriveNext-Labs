package ru.mtuci.drivenext.presentation.catalog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.data.cars.CarRepository
import ru.mtuci.drivenext.databinding.ActivityMainBinding

class SearchResultsActivity : AppCompatActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        val repository = CarRepository(); val adapter = CarAdapter({ Toast.makeText(this, "Детали", Toast.LENGTH_SHORT).show() }, { Toast.makeText(this, "Бронирование", Toast.LENGTH_SHORT).show() })
        binding.title.text = "Результаты поиска"; binding.bottomNavigation.visibility = View.GONE
        binding.carList.layoutManager = LinearLayoutManager(this); binding.carList.adapter = adapter
        fun search(q: String) { binding.progress.visibility = View.VISIBLE; binding.carList.postDelayed({ adapter.submitList(repository.search(q)); binding.progress.visibility = View.GONE }, 300) }
        val query = intent.getStringExtra("query").orEmpty(); binding.searchView.setQuery(query, false); search(query)
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener { override fun onQueryTextSubmit(query: String) = true; override fun onQueryTextChange(text: String): Boolean { search(text); return true } })
    }
}
