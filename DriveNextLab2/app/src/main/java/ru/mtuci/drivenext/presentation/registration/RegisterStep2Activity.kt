package ru.mtuci.drivenext.presentation.registration

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.registration.RegistrationDraft
import ru.mtuci.drivenext.databinding.ActivityRegisterStep2Binding
import ru.mtuci.drivenext.presentation.common.showDatePicker

class RegisterStep2Activity : AppCompatActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.surnameInput.setText(RegistrationDraft.surname)
        binding.nameInput.setText(RegistrationDraft.name)
        binding.patronymicInput.setText(RegistrationDraft.patronymic)
        binding.birthDateInput.setText(RegistrationDraft.birthDate)
        if (RegistrationDraft.gender == "male") binding.maleRadio.isChecked = true
        if (RegistrationDraft.gender == "female") binding.femaleRadio.isChecked = true
        binding.backButton.setOnClickListener { finish() }
        // Дата выбирается в календаре и записывается в текстовое поле.
        binding.birthDateInput.setOnClickListener { showDatePicker(getString(R.string.birth_date), "MM/dd/yyyy") { binding.birthDateInput.setText(it) } }
        binding.nextButton.setOnClickListener {
            // Фамилия, имя, дата рождения и пол обязательны.
            val surname = binding.surnameInput.text.toString().trim()
            val name = binding.nameInput.text.toString().trim()
            val birth = binding.birthDateInput.text.toString()
            val gender = when (binding.genderGroup.checkedRadioButtonId) { R.id.maleRadio -> "male"; R.id.femaleRadio -> "female"; else -> "" }
            if (surname.isBlank() || name.isBlank() || birth.isBlank() || gender.isBlank()) {
                Snackbar.make(binding.root, R.string.required_fields, Snackbar.LENGTH_SHORT).show()
            } else {
                // Сохраняем персональные данные перед последним шагом.
                RegistrationDraft.surname = surname; RegistrationDraft.name = name
                RegistrationDraft.patronymic = binding.patronymicInput.text.toString().trim()
                RegistrationDraft.birthDate = birth; RegistrationDraft.gender = gender
                startActivity(Intent(this, RegisterStep3Activity::class.java))
            }
        }
    }
}
