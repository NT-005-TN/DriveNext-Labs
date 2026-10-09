package ru.mtuci.drivenext.presentation.connection

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.databinding.ActivityNoConnectionBinding
import ru.mtuci.drivenext.presentation.auth.LoginActivity
import ru.mtuci.drivenext.presentation.onboarding.OnboardingActivity

class NoConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoConnectionBinding
    private val viewModel: NoConnectionViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                NoConnectionViewModel(NetworkMonitor(applicationContext)) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.retryButton.setOnClickListener {
            // Повторно проверяем сеть перед переходом дальше.
            if (viewModel.hasInternetConnection()) {
                if (intent.getBooleanExtra("return_to_caller", false)) {
                    finish()
                    return@setOnClickListener
                }
                val target = if (OnboardingPreferences(this).isCompleted()) {
                    LoginActivity::class.java
                } else {
                    OnboardingActivity::class.java
                }
                startActivity(Intent(this, target))
                finish()
            } else {
                Snackbar.make(binding.root, R.string.no_connection_message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
