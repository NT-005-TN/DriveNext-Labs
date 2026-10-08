package ru.mtuci.drivenext.data.preferences

import android.content.Context

class OnboardingPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = preferences.getBoolean(KEY_COMPLETED, false)

    fun markCompleted() {
        preferences.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    private companion object {
        const val PREFS_NAME = "drive_next_preferences"
        const val KEY_COMPLETED = "onboarding_completed"
    }
}
