package ru.mtuci.drivenext.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.databinding.ActivitySplashBinding
import ru.mtuci.drivenext.domain.ResolveStartDestinationUseCase
import ru.mtuci.drivenext.domain.StartDestination
import ru.mtuci.drivenext.presentation.auth.LoginActivity
import ru.mtuci.drivenext.presentation.connection.NoConnectionActivity
import ru.mtuci.drivenext.presentation.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding // Хранит ссылки на элементы экрана; заполняется в onCreate.
    private val viewModel: SplashViewModel by viewModels { // Получает ViewModel через фабрику и сохраняет её при пересоздании экрана.
        val useCase = ResolveStartDestinationUseCase( // Создаёт правило выбора следующего экрана.
            NetworkMonitor(applicationContext), // Передаёт объект проверки сети в правило запуска.
            OnboardingPreferences(applicationContext), // Передаёт доступ к сохранённому флагу онбординга.
        )
        object : ViewModelProvider.Factory { // Задаёт способ создания ViewModel с зависимостями.
            @Suppress("UNCHECKED_CAST") // Убирает предупреждение о приведении конкретной ViewModel к типу T.
            override fun <T : ViewModel> create(modelClass: Class<T>): T = SplashViewModel(useCase) as T // Создаёт ViewModel с правилом запуска и возвращает её фабрике.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Подготавливает новый экран при вызове Android.
        installSplashScreen() // Подключает системную заставку перед созданием Activity.
        super.onCreate(savedInstanceState) // Выполняет стандартную инициализацию Activity с полученным состоянием.
        binding = ActivitySplashBinding.inflate(layoutInflater) // Создаёт элементы из XML и сохраняет ссылки на них.
        setContentView(binding.root) // Устанавливает созданную разметку как содержимое Activity.

        lifecycleScope.launch { // Запускает корутину, отменяемую при уничтожении Activity.
            delay(SPLASH_DURATION_MS) // Ждёт 2,5 секунды, не блокируя интерфейс.
            val target = when (viewModel.destination()) { // Получает решение и выбирает класс следующей Activity.
                StartDestination.NO_CONNECTION -> NoConnectionActivity::class.java // Выбирает экран ошибки сети.
                StartDestination.ONBOARDING -> OnboardingActivity::class.java // Выбирает вводные страницы.
                StartDestination.LOGIN -> LoginActivity::class.java // Выбирает экран входа.
            }
            startActivity(Intent(this@SplashActivity, target)) // Просит Android открыть выбранный экран из текущей Activity.
            finish() // Закрывает текущую Activity и убирает её из истории.
        }
    }

    private companion object { const val SPLASH_DURATION_MS = 2_500L } // Задаёт длительность заставки в миллисекундах.
}
