package ru.mtuci.drivenext.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityLoginBinding
import ru.mtuci.drivenext.presentation.common.afterTextChanged
import ru.mtuci.drivenext.presentation.main.MainActivity
import ru.mtuci.drivenext.presentation.registration.RegisterStep1Activity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fun updateState() {
            binding.signInButton.isEnabled = binding.emailInput.text?.isNotBlank() == true && binding.passwordInput.text?.isNotBlank() == true
        }
        binding.emailInput.afterTextChanged(::updateState)
        binding.passwordInput.afterTextChanged(::updateState)
        binding.signInButton.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.text.toString()).matches()) {
                binding.emailLayout.error = getString(R.string.invalid_email)
            } else {
                getSharedPreferences("session", MODE_PRIVATE).edit().putString("access_token", "demo_token").apply()
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
        }
        binding.googleButton.setOnClickListener { Toast.makeText(this, R.string.google_sign_in, Toast.LENGTH_SHORT).show() }
        binding.registerLink.setOnClickListener { startActivity(Intent(this, RegisterStep1Activity::class.java)) }
    }
}
