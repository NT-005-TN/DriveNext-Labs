package ru.mtuci.drivenext.presentation.registration

import android.os.Bundle
import ru.mtuci.drivenext.data.registration.RegistrationDraft as Draft
import ru.mtuci.drivenext.databinding.ActivityRegisterStep1Binding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.common.afterTextChanged

class RegisterStep1Activity : AuthActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailInput.setText(Draft.email)
        binding.passwordInput.setText(Draft.password)
        binding.repeatPasswordInput.setText(Draft.repeatPassword)
        binding.termsCheckbox.isChecked = Draft.terms
        binding.emailInput.afterTextChanged { binding.emailLayout.error = null }
        binding.backButton.setOnClickListener { finish() }
        binding.nextButton.setOnClickListener {
            val error = authModel.step1(binding.emailInput.text.toString(), binding.passwordInput.text.toString(),
                binding.repeatPasswordInput.text.toString(), binding.termsCheckbox.isChecked)
            if (error != null) message(error) else navigate(RegisterStep2Activity::class.java)
        }
    }
}
