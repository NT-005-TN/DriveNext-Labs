package ru.mtuci.drivenext

import android.app.Application
import ru.mtuci.drivenext.data.auth.SecureStore
import ru.mtuci.drivenext.data.auth.SupabaseAuthRepository
import ru.mtuci.drivenext.data.registration.RegistrationDraft
import ru.mtuci.drivenext.domain.AuthUseCase

class DriveNextApplication : Application() {
    lateinit var secureStore: SecureStore
        private set
    lateinit var auth: AuthUseCase
        private set
    override fun onCreate() {
        super.onCreate()
        secureStore = SecureStore(this)
        auth = AuthUseCase(SupabaseAuthRepository(this, secureStore))
        RegistrationDraft.initialize(secureStore)
        // Удаляем только старую демонстрационную сессию, не пользовательские настройки.
        getSharedPreferences("session", MODE_PRIVATE).edit().remove("access_token").apply()
    }
}
