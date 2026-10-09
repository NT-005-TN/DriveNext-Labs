package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityPlaceholderBinding
import ru.mtuci.drivenext.presentation.auth.AuthActivity

class MainActivity : AuthActivity() {
    private lateinit var binding: ActivityPlaceholderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.setText(R.string.main_title)
        binding.subtitle.text = "Проверяем сессию…"
        (binding.root as LinearLayout).addView(Button(this).apply {
            text = "Выйти из аккаунта"
            setOnClickListener { authModel.logout() }
        })
    }
    override fun onStart() {
        super.onStart()
        // Проверяем сессию также после возврата с экрана отсутствия сети.
        authModel.restore(force = true, protectedScreen = true)
    }
    override fun onAuthenticated() { binding.subtitle.text = "Вы вошли в аккаунт Supabase" }
}
