package ru.mtuci.drivenext.presentation.registration

import android.os.Bundle
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.registration.RegistrationDraft as Draft
import ru.mtuci.drivenext.databinding.ActivityRegisterStep2Binding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.common.afterTextChanged
import ru.mtuci.drivenext.presentation.common.showDatePicker

class RegisterStep2Activity : AuthActivity() {
    private lateinit var binding: ActivityRegisterStep2Binding
    private fun gender() = when (binding.genderGroup.checkedRadioButtonId) { R.id.maleRadio -> "male"; R.id.femaleRadio -> "female"; else -> "" }
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.surnameInput.setText(Draft.surname); binding.nameInput.setText(Draft.name)
        binding.patronymicInput.setText(Draft.patronymic); binding.birthDateInput.setText(Draft.birthDate)
        if (Draft.gender == "male") binding.maleRadio.isChecked = true
        if (Draft.gender == "female") binding.femaleRadio.isChecked = true
        fun updateState() {
            binding.nextButton.isEnabled = authModel.personalFilled(binding.surnameInput.text.toString(),
                binding.nameInput.text.toString(), binding.birthDateInput.text.toString(), gender())
        }
        binding.surnameInput.afterTextChanged(::updateState); binding.nameInput.afterTextChanged(::updateState)
        binding.birthDateInput.afterTextChanged(::updateState)
        binding.genderGroup.setOnCheckedChangeListener { _, _ -> updateState() }
        updateState()
        binding.backButton.setOnClickListener { finish() }
        binding.birthDateInput.setOnClickListener { showDatePicker(getString(R.string.birth_date), "MM/dd/yyyy") { binding.birthDateInput.setText(it) } }
        binding.nextButton.setOnClickListener {
            val error = authModel.step2(binding.surnameInput.text.toString(), binding.nameInput.text.toString(), binding.birthDateInput.text.toString(), gender())
            if (error != null) message(error) else { save(); navigate(RegisterStep3Activity::class.java) }
        }
    }
    private fun save() = authModel.savePersonal(binding.surnameInput.text.toString(), binding.nameInput.text.toString(),
        binding.patronymicInput.text.toString(), binding.birthDateInput.text.toString(), gender())
    override fun onStop() { save(); super.onStop() }
}
