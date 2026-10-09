package ru.mtuci.drivenext.presentation.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.registration.RegistrationDraft as Draft
import ru.mtuci.drivenext.databinding.ActivityRegisterStep3Binding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.common.afterTextChanged
import ru.mtuci.drivenext.presentation.common.showDatePicker

class RegisterStep3Activity : AuthActivity() {
    private lateinit var binding: ActivityRegisterStep3Binding
    private var target = "PROFILE"
    private val picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            catch (_: SecurityException) { message("При повторном запуске выберите фото заново: устройство не сохранило доступ.") }
            authModel.savePhoto(target, uri.toString())
            renderPhotos()
            updateState()
        }
    }
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        binding = ActivityRegisterStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        target = state?.getString("photo_target") ?: "PROFILE"
        binding.licenseInput.setText(Draft.license); binding.issueDateInput.setText(Draft.issueDate)
        binding.licenseInput.afterTextChanged(::updateState); binding.issueDateInput.afterTextChanged(::updateState)
        renderPhotos(); updateState()
        binding.backButton.setOnClickListener { finish() }
        binding.issueDateInput.setOnClickListener { showDatePicker(getString(R.string.issue_date), "dd/MM/yyyy") { binding.issueDateInput.setText(it) } }
        binding.profilePhotoButton.setOnClickListener { pick("PROFILE") }
        binding.licensePhotoButton.setOnClickListener { pick("LICENSE") }
        binding.passportPhotoButton.setOnClickListener { pick("PASSPORT") }
        binding.nextButton.setOnClickListener {
            if (Draft.password.isBlank()) {
                message("После перезапуска введите пароль на первом шаге. Остальные данные сохранены.")
                navigate(RegisterStep1Activity::class.java)
            } else authModel.submit()
        }
    }
    private fun renderPhotos() {
        Draft.profilePhoto?.let { runCatching { binding.profilePhotoButton.setImageURI(Uri.parse(it)) } }
        binding.licensePhotoButton.setText(if (Draft.licensePhoto == null) R.string.upload_photo else R.string.photo_selected)
        binding.passportPhotoButton.setText(if (Draft.passportPhoto == null) R.string.upload_photo else R.string.photo_selected)
    }
    private fun updateState() {
        authModel.saveDocuments(binding.licenseInput.text.toString(), binding.issueDateInput.text.toString())
        binding.nextButton.isEnabled = authModel.documentsFilled()
    }
    private fun pick(value: String) { target = value; picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("photo_target", target); super.onSaveInstanceState(outState) }
}
