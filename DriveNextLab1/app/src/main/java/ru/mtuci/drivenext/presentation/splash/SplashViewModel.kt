package ru.mtuci.drivenext.presentation.splash

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.domain.ResolveStartDestinationUseCase
import ru.mtuci.drivenext.domain.StartDestination

class SplashViewModel(private val resolveStartDestination: ResolveStartDestinationUseCase) : ViewModel() {
    fun destination(): StartDestination = resolveStartDestination.execute() // Возвращает результат правила выбора стартового экрана.
}
