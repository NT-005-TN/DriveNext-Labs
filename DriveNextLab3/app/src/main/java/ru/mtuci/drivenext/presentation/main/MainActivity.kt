package ru.mtuci.drivenext.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.data.cars.CarRepository
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.presentation.catalog.CarAdapter
import ru.mtuci.drivenext.presentation.catalog.SearchResultsActivity
import ru.mtuci.drivenext.presentation.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository = CarRepository()
        val adapter = CarAdapter({ Toast.makeText(this, "Детали: ${it.brand} ${it.model}", Toast.LENGTH_SHORT).show() }, { Toast.makeText(this, "Бронирование: ${it.brand} ${it.model}", Toast.LENGTH_SHORT).show() })
        binding.carList.layoutManager = LinearLayoutManager(this); binding.carList.adapter = adapter
        binding.progress.visibility = View.VISIBLE
        binding.carList.postDelayed({ adapter.submitList(repository.all()); binding.progress.visibility = View.GONE }, 450)
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { startActivity(Intent(this@MainActivity, SearchResultsActivity::class.java).putExtra("query", query)); return true }
            override fun onQueryTextChange(newText: String): Boolean = false
        })
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) { ru.mtuci.drivenext.R.id.navSettings -> startActivity(Intent(this, SettingsActivity::class.java)); ru.mtuci.drivenext.R.id.navFavorites -> Toast.makeText(this, "Избранное будет в ЛР №4", Toast.LENGTH_SHORT).show() }
            true
        }
    }
}
