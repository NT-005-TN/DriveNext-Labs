package ru.mtuci.drivenext.data.preferences

import android.content.Context

class OnboardingPreferences(context: Context) {
    // MODE_PRIVATE делает настройки доступными только этому приложению.
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // При первом запуске ключа нет, поэтому возвращаем false.
    fun isCompleted(): Boolean = preferences.getBoolean(KEY_COMPLETED, false)

    fun markCompleted() {
        // apply записывает флаг асинхронно без блокировки интерфейса.
        preferences.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    private companion object {
        const val PREFS_NAME = "drive_next_preferences"
        const val KEY_COMPLETED = "onboarding_completed"
    }
}
