package ru.mtuci.drivenext.domain

import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences

class ResolveStartDestinationUseCase(
    private val networkMonitor: NetworkMonitor,
    private val onboardingPreferences: OnboardingPreferences,
) {
    fun execute(): StartDestination = when { // Проверяет условия по порядку и возвращает первый подходящий маршрут.
        !networkMonitor.hasInternetConnection() -> StartDestination.NO_CONNECTION // При отсутствии интернета возвращает маршрут ошибки сети.
        !onboardingPreferences.isCompleted() -> StartDestination.ONBOARDING // Если онбординг не пройден, возвращает маршрут вводных страниц.
        else -> StartDestination.LOGIN // При наличии сети и пройденном онбординге возвращает маршрут входа.
    }
}

enum class StartDestination { NO_CONNECTION, ONBOARDING, LOGIN } // Перечисляет допустимые варианты следующего экрана.
