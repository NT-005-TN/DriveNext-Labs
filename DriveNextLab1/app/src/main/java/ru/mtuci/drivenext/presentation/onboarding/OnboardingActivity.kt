package ru.mtuci.drivenext.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.databinding.ActivityOnboardingBinding
import ru.mtuci.drivenext.presentation.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding // Хранит ссылки на элементы экрана; заполняется в onCreate.
    private val viewModel: OnboardingViewModel by viewModels { // Получает ViewModel через фабрику и сохраняет её при пересоздании экрана.
        object : ViewModelProvider.Factory { // Задаёт способ создания ViewModel с зависимостями.
            @Suppress("UNCHECKED_CAST") // Убирает предупреждение о приведении конкретной ViewModel к типу T.
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                OnboardingViewModel(OnboardingPreferences(applicationContext)) as T // Создаёт ViewModel с доступом к настройкам онбординга.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Подготавливает новый экран при вызове Android.
        super.onCreate(savedInstanceState) // Выполняет стандартную инициализацию Activity с полученным состоянием.
        binding = ActivityOnboardingBinding.inflate(layoutInflater) // Создаёт элементы из XML и сохраняет ссылки на них.
        setContentView(binding.root) // Устанавливает созданную разметку как содержимое Activity.

        binding.viewPager.adapter = OnboardingAdapter(this, viewModel.pages) // Подключает к ViewPager2 адаптер со списком страниц.
        createIndicators() // Создаёт начальные точки индикатора.
        updateControls(0) // Настраивает кнопку и индикаторы для первой страницы.

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() { // Подписывается на переключение страниц.
            override fun onPageSelected(position: Int) = updateControls(position) // Обновляет элементы управления по индексу выбранной страницы.
        })
        binding.skipButton.setOnClickListener { finishOnboarding() } // Завершает онбординг при нажатии «Пропустить».
        binding.nextButton.setOnClickListener { // Назначает переход вперёд на нажатие кнопки.
            val next = binding.viewPager.currentItem + 1 // Вычисляет индекс следующей страницы.
            if (next < viewModel.pages.size) binding.viewPager.currentItem = next else finishOnboarding() // Переключает страницу, а после последней завершает онбординг.
        }
    }

    private fun createIndicators() { // Добавляет первоначальные индикаторы страниц.
        repeat(viewModel.pages.size) { // Повторяет создание точки по числу страниц.
            binding.indicators.addView(View(this), LinearLayout.LayoutParams(dp(9), dp(9)).apply { setMargins(dp(4)) }) // Добавляет точку 9×9 dp с отступами 4 dp.
        }
    }

    private fun updateControls(position: Int) { // Обновляет надпись кнопки и выделение текущей страницы.
        val lastPage = position == viewModel.pages.lastIndex // Определяет, выбрана ли последняя страница.
        binding.nextButton.setText(if (lastPage) R.string.start else R.string.next) // Показывает «Поехали» на последней странице, иначе «Далее».
        binding.indicators.removeAllViews() // Удаляет предыдущие индикаторы из контейнера.
        viewModel.pages.indices.forEach { index -> // Создаёт индикатор для каждого индекса страницы.
            val active = index == position // Определяет, относится ли точка к текущей странице.
            binding.indicators.addView(View(this).apply { // Создаёт View точки, настраивает её и добавляет в контейнер.
                background = ContextCompat.getDrawable( // Загружает drawable и назначает его фоном точки.
                    this@OnboardingActivity, // Передаёт контекст экрана для загрузки ресурса.
                    if (active) R.drawable.bg_indicator_active else R.drawable.bg_indicator_inactive, // Выбирает активный или неактивный фон точки.
                )
            }, LinearLayout.LayoutParams(dp(if (active) 34 else 9), dp(if (active) 6 else 9)).apply { setMargins(dp(4)) }) // Задаёт размер точки и отступы; активная точка шире.
        }
    }

    private fun finishOnboarding() { // Сохраняет прохождение и переводит пользователя на вход.
        viewModel.complete() // Просит ViewModel сохранить флаг прохождения.
        startActivity(Intent(this, LoginActivity::class.java)) // Открывает заглушку входа после онбординга.
        finish() // Закрывает текущую Activity и убирает её из истории.
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt() // Переводит dp в пиксели с учётом плотности экрана.
}
