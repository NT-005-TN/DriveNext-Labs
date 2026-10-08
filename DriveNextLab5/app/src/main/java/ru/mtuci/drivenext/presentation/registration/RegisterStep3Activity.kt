package ru.mtuci.drivenext.presentation.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityRegisterStep3Binding
import ru.mtuci.drivenext.presentation.common.showDatePicker

class RegisterStep3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterStep3Binding
    private var target = PhotoTarget.PROFILE
    private var licensePhoto: Uri? = null
    private var passportPhoto: Uri? = null
    private val picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri ?: return@registerForActivityResult
        when (target) {
            PhotoTarget.PROFILE -> binding.profilePhotoButton.setImageURI(uri)
            PhotoTarget.LICENSE -> { licensePhoto = uri; binding.licensePhotoButton.text = "Фото выбрано" }
            PhotoTarget.PASSPORT -> { passportPhoto = uri; binding.passportPhotoButton.text = "Фото выбрано" }
        }
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        binding = ActivityRegisterStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }
        binding.issueDateInput.setOnClickListener { showDatePicker(getString(R.string.issue_date), "dd/MM/yyyy") { binding.issueDateInput.setText(it) } }
        binding.profilePhotoButton.setOnClickListener { pick(PhotoTarget.PROFILE) }
        binding.licensePhotoButton.setOnClickListener { pick(PhotoTarget.LICENSE) }
        binding.passportPhotoButton.setOnClickListener { pick(PhotoTarget.PASSPORT) }
        binding.nextButton.setOnClickListener {
            when {
                binding.licenseInput.text.toString().length != 10 -> Snackbar.make(binding.root, R.string.invalid_license, Snackbar.LENGTH_SHORT).show()
                binding.issueDateInput.text.isNullOrBlank() -> Snackbar.make(binding.root, R.string.invalid_issue_date, Snackbar.LENGTH_SHORT).show()
                licensePhoto == null || passportPhoto == null -> Snackbar.make(binding.root, R.string.photos_required, Snackbar.LENGTH_SHORT).show()
                else -> startActivity(Intent(this, SuccessActivity::class.java))
            }
        }
    }

    private fun pick(photoTarget: PhotoTarget) {
        target = photoTarget
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private enum class PhotoTarget { PROFILE, LICENSE, PASSPORT }
}
