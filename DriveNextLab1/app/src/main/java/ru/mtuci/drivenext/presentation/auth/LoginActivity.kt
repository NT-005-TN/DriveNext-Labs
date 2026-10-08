package ru.mtuci.drivenext.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityPlaceholderBinding

// Заглушка входа для первой лабораторной.
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Создаём экран из XML и получаем ссылки на его элементы.
        val binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.setText(R.string.login_title)
        binding.subtitle.setText(R.string.login_placeholder)
    }
}
