package ru.mtuci.drivenext.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // Получает системную службу подключения к сети.

    fun hasInternetConnection(): Boolean { // Проверяет наличие сети с подтверждённым интернетом.
        val network = connectivityManager.activeNetwork ?: return false // Получает активную сеть; при её отсутствии возвращает false.
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false // Получает свойства сети; при их отсутствии возвращает false.
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && // Проверяет, предназначена ли сеть для доступа в интернет.
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) // Дополнительно требует подтверждения доступа от Android.
    }
}
