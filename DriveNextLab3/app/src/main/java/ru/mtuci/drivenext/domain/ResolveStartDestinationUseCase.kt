package ru.mtuci.drivenext.domain

import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences

class ResolveStartDestinationUseCase(
    private val networkMonitor: NetworkMonitor,
    private val onboardingPreferences: OnboardingPreferences,
) {
    // Условия проверяются по порядку: сеть, онбординг, затем авторизация.
    fun execute(): StartDestination = when {
        !networkMonitor.hasInternetConnection() -> StartDestination.NO_CONNECTION
        !onboardingPreferences.isCompleted() -> StartDestination.ONBOARDING
        else -> StartDestination.LOGIN
    }
}

// Enum ограничивает набор допустимых стартовых маршрутов.
enum class StartDestination { NO_CONNECTION, ONBOARDING, LOGIN }
