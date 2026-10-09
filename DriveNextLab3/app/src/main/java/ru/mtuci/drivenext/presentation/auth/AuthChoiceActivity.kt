package ru.mtuci.drivenext.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivityAuthChoiceBinding
import ru.mtuci.drivenext.presentation.registration.RegisterStep1Activity

class AuthChoiceActivity : AuthActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authModel.restore()
        // Пользователь выбирает вход или создание нового аккаунта.
        binding.signInButton.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
        binding.registerButton.setOnClickListener { startActivity(Intent(this, RegisterStep1Activity::class.java)) }
    }
}
