package ru.mtuci.drivenext.presentation.onboarding

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences

class OnboardingViewModel(private val preferences: OnboardingPreferences) : ViewModel() {
    val pages = listOf(
        OnboardingPage(R.drawable.onboarding_car_sharing, R.string.slide_1_title, R.string.slide_1_description),
        OnboardingPage(R.drawable.onboarding_safe, R.string.slide_2_title, R.string.slide_2_description),
        OnboardingPage(R.drawable.onboarding_offers, R.string.slide_3_title, R.string.slide_3_description),
    )

    fun complete() = preferences.markCompleted()
}
