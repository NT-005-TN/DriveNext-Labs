package ru.mtuci.drivenext.presentation.connection

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor

class NoConnectionViewModel(private val networkMonitor: NetworkMonitor) : ViewModel() {
    fun hasInternetConnection(): Boolean = networkMonitor.hasInternetConnection() // Возвращает результат проверки из NetworkMonitor.
}
