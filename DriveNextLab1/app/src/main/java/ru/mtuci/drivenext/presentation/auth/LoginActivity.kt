package ru.mtuci.drivenext.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityPlaceholderBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // Подготавливает новый экран при вызове Android.
        super.onCreate(savedInstanceState) // Выполняет стандартную инициализацию Activity с полученным состоянием.
        val binding = ActivityPlaceholderBinding.inflate(layoutInflater) // Создаёт элементы из XML и сохраняет ссылки на них.
        setContentView(binding.root) // Устанавливает созданную разметку как содержимое Activity.
        binding.title.setText(R.string.login_title) // Устанавливает заголовок входа из strings.xml.
        binding.subtitle.setText(R.string.login_placeholder) // Показывает пояснение, что вход пока является заглушкой.
    }
}
