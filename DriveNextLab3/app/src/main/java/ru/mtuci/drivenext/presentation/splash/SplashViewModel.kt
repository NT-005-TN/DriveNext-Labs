package ru.mtuci.drivenext.presentation.splash

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.domain.ResolveStartDestinationUseCase
import ru.mtuci.drivenext.domain.StartDestination

class SplashViewModel(private val resolveStartDestination: ResolveStartDestinationUseCase) : ViewModel() {
    // Делегируем выбор следующего экрана бизнес-правилу запуска.
    fun destination(): StartDestination = resolveStartDestination.execute()
}
