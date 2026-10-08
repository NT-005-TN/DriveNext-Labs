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
    // Ссылки на кнопки, страницы и индикаторы.
    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                OnboardingViewModel(OnboardingPreferences(applicationContext)) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Адаптер создаёт страницы для перелистывания.
        binding.viewPager.adapter = OnboardingAdapter(this, viewModel.pages)
        createIndicators()
        updateControls(0)

        // При смене страницы обновляем кнопку и индикаторы.
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateControls(position)
        })
        binding.skipButton.setOnClickListener { finishOnboarding() }
        binding.nextButton.setOnClickListener {
            val next = binding.viewPager.currentItem + 1
            if (next < viewModel.pages.size) binding.viewPager.currentItem = next else finishOnboarding()
        }
    }

    private fun createIndicators() {
        repeat(viewModel.pages.size) {
            binding.indicators.addView(View(this), LinearLayout.LayoutParams(dp(9), dp(9)).apply { setMargins(dp(4)) })
        }
    }

    // Перерисовываем точки и меняем «Далее» на «Поехали».
    private fun updateControls(position: Int) {
        val lastPage = position == viewModel.pages.lastIndex
        binding.nextButton.setText(if (lastPage) R.string.start else R.string.next)
        binding.indicators.removeAllViews()
        viewModel.pages.indices.forEach { index ->
            val active = index == position
            binding.indicators.addView(View(this).apply {
                background = ContextCompat.getDrawable(
                    this@OnboardingActivity,
                    if (active) R.drawable.bg_indicator_active else R.drawable.bg_indicator_inactive,
                )
            }, LinearLayout.LayoutParams(dp(if (active) 34 else 9), dp(if (active) 6 else 9)).apply { setMargins(dp(4)) })
        }
    }

    // Сохраняем прохождение и открываем вход.
    private fun finishOnboarding() {
        viewModel.complete()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Переводим dp в пиксели.
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
