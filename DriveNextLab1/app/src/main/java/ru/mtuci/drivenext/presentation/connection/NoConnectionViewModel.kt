package ru.mtuci.drivenext.presentation.connection

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor

class NoConnectionViewModel(private val networkMonitor: NetworkMonitor) : ViewModel() {
    // Передаём проверку сети в NetworkMonitor.
    fun hasInternetConnection(): Boolean = networkMonitor.hasInternetConnection()
}
