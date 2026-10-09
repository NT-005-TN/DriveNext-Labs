package ru.mtuci.drivenext.presentation.connection

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.databinding.ActivityNoConnectionBinding
import ru.mtuci.drivenext.presentation.auth.LoginActivity
import ru.mtuci.drivenext.presentation.onboarding.OnboardingActivity

class NoConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoConnectionBinding // Хранит ссылки на элементы экрана; заполняется в onCreate.
    private val viewModel: NoConnectionViewModel by viewModels { // Получает ViewModel через фабрику и сохраняет её при пересоздании экрана.
        object : ViewModelProvider.Factory { // Задаёт способ создания ViewModel с зависимостями.
            @Suppress("UNCHECKED_CAST") // Убирает предупреждение о приведении конкретной ViewModel к типу T.
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                NoConnectionViewModel(NetworkMonitor(applicationContext)) as T // Создаёт ViewModel и передаёт ей проверку сети.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Подготавливает новый экран при вызове Android.
        super.onCreate(savedInstanceState) // Выполняет стандартную инициализацию Activity с полученным состоянием.
        binding = ActivityNoConnectionBinding.inflate(layoutInflater) // Создаёт элементы из XML и сохраняет ссылки на них.
        setContentView(binding.root) // Устанавливает созданную разметку как содержимое Activity.

        binding.retryButton.setOnClickListener { // Назначает повторную проверку сети на нажатие кнопки.
            if (viewModel.hasInternetConnection()) { // Проверяет, появился ли интернет к моменту нажатия.
                val target = if (OnboardingPreferences(this).isCompleted()) { // Выбирает экран по сохранённому прохождению онбординга.
                    LoginActivity::class.java // Возвращает класс экрана входа.
                } else {
                    OnboardingActivity::class.java // Возвращает класс вводных страниц.
                }
                startActivity(Intent(this, target)) // Открывает Activity, класс которой сохранён в target.
                finish() // Закрывает текущую Activity и убирает её из истории.
            } else {
                Snackbar.make(binding.root, R.string.no_connection_message, Snackbar.LENGTH_SHORT).show() // Показывает короткое сообщение об отсутствии сети.
            }
        }
    }
}
