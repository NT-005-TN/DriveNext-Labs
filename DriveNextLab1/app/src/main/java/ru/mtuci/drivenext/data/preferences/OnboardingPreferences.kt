package ru.mtuci.drivenext.data.preferences

import android.content.Context
import androidx.core.content.edit

class OnboardingPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // Открывает приватное хранилище настроек по имени PREFS_NAME.

    fun isCompleted(): Boolean = preferences.getBoolean(KEY_COMPLETED, false) // Читает флаг прохождения; без записи возвращает false.

    fun markCompleted() { // Сохраняет, что вводные страницы уже пройдены.
        preferences.edit { putBoolean(KEY_COMPLETED, true) } // Записывает true под ключом прохождения онбординга.
    }

    private companion object {
        const val PREFS_NAME = "drive_next_preferences" // Задаёт имя хранилища настроек.
        const val KEY_COMPLETED = "onboarding_completed" // Задаёт ключ флага прохождения.
    }
}
