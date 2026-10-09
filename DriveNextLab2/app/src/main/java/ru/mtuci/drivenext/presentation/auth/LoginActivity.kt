package ru.mtuci.drivenext.presentation.auth

import android.content.Intent
import android.os.Bundle
import ru.mtuci.drivenext.databinding.ActivityLoginBinding
import ru.mtuci.drivenext.presentation.common.afterTextChanged
import ru.mtuci.drivenext.presentation.registration.RegisterStep1Activity

class LoginActivity : AuthActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fun updateState() {
            binding.signInButton.isEnabled = !binding.emailInput.text.isNullOrBlank() && !binding.passwordInput.text.isNullOrBlank()
            binding.emailLayout.error = null
        }
        binding.emailInput.afterTextChanged(::updateState)
        binding.passwordInput.afterTextChanged(::updateState)
        updateState()
        binding.signInButton.setOnClickListener { authModel.login(binding.emailInput.text.toString(), binding.passwordInput.text.toString()) }
        binding.googleButton.setOnClickListener { authModel.google() }
        binding.registerLink.setOnClickListener { navigate(RegisterStep1Activity::class.java) }
        binding.forgotPassword.setOnClickListener { authModel.recover(binding.emailInput.text.toString()) }
        if (savedInstanceState == null) intent.data?.toString()?.let(authModel::callback)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.toString()?.let(authModel::callback)
    }
}
