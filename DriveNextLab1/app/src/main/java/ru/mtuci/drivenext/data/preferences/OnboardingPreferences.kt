package ru.mtuci.drivenext.data.preferences

import android.content.Context
import androidx.core.content.edit

class OnboardingPreferences(context: Context) {
    // Храним прохождение онбординга между запусками.
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = preferences.getBoolean(KEY_COMPLETED, false)

    fun markCompleted() {
        preferences.edit { putBoolean(KEY_COMPLETED, true) }
    }

    private companion object {
        const val PREFS_NAME = "drive_next_preferences"
        const val KEY_COMPLETED = "onboarding_completed"
    }
}
