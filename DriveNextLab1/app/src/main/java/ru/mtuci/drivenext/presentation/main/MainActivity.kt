package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // Подготавливает новый экран при вызове Android.
        super.onCreate(savedInstanceState) // Выполняет стандартную инициализацию Activity с полученным состоянием.
        val binding = ActivityMainBinding.inflate(layoutInflater) // Создаёт элементы из XML и сохраняет ссылки на них.
        setContentView(binding.root) // Устанавливает созданную разметку как содержимое Activity.
    }
}
