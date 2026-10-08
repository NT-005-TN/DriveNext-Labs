package ru.mtuci.drivenext.domain

import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences

class ResolveStartDestinationUseCase(
    private val networkMonitor: NetworkMonitor,
    private val onboardingPreferences: OnboardingPreferences,
) {
    // Выбираем стартовый экран по состоянию сети и онбординга.
    fun execute(): StartDestination = when {
        !networkMonitor.hasInternetConnection() -> StartDestination.NO_CONNECTION
        !onboardingPreferences.isCompleted() -> StartDestination.ONBOARDING
        else -> StartDestination.LOGIN
    }
}

enum class StartDestination { NO_CONNECTION, ONBOARDING, LOGIN }
