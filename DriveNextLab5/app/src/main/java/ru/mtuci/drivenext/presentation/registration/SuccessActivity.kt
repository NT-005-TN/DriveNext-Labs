package ru.mtuci.drivenext.presentation.registration

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.data.registration.RegistrationDraft
import ru.mtuci.drivenext.databinding.ActivitySuccessBinding
import ru.mtuci.drivenext.presentation.main.MainActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            RegistrationDraft.clear()
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
    }
}
