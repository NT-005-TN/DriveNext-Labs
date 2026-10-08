package ru.mtuci.drivenext.presentation.registration

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.registration.RegistrationDraft
import ru.mtuci.drivenext.databinding.ActivityRegisterStep1Binding

class RegisterStep1Activity : AppCompatActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailInput.setText(RegistrationDraft.email)
        binding.passwordInput.setText(RegistrationDraft.password)
        binding.backButton.setOnClickListener { finish() }
        binding.nextButton.setOnClickListener {
            // Проверяем почту, пароли и согласие с условиями.
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            val repeat = binding.repeatPasswordInput.text.toString()
            when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.emailLayout.error = getString(R.string.invalid_email)
                password.isBlank() || repeat.isBlank() -> Snackbar.make(binding.root, R.string.required_fields, Snackbar.LENGTH_SHORT).show()
                password != repeat -> Snackbar.make(binding.root, R.string.passwords_mismatch, Snackbar.LENGTH_SHORT).show()
                !binding.termsCheckbox.isChecked -> Snackbar.make(binding.root, R.string.terms_required, Snackbar.LENGTH_SHORT).show()
                else -> {
                    // Сохраняем первый шаг и открываем персональные данные.
                    RegistrationDraft.email = email; RegistrationDraft.password = password
                    startActivity(Intent(this, RegisterStep2Activity::class.java))
                }
            }
        }
    }
}
