package ru.mtuci.drivenext.presentation.onboarding

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences

class OnboardingViewModel(private val preferences: OnboardingPreferences) : ViewModel() {
    val pages = listOf( // Собирает данные трёх вводных страниц в список.
        OnboardingPage(R.drawable.onboarding_car_sharing, R.string.slide_1_title, R.string.slide_1_description), // Связывает первую картинку с заголовком и описанием.
        OnboardingPage(R.drawable.onboarding_safe, R.string.slide_2_title, R.string.slide_2_description), // Задаёт ресурсы второй страницы.
        OnboardingPage(R.drawable.onboarding_offers, R.string.slide_3_title, R.string.slide_3_description), // Задаёт ресурсы третьей страницы.
    )

    fun complete() = preferences.markCompleted() // Передаёт сохранение результата в OnboardingPreferences.
}
