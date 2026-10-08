package ru.mtuci.drivenext.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.databinding.ActivitySplashBinding
import ru.mtuci.drivenext.domain.ResolveStartDestinationUseCase
import ru.mtuci.drivenext.domain.StartDestination
import ru.mtuci.drivenext.presentation.auth.LoginActivity
import ru.mtuci.drivenext.presentation.connection.NoConnectionActivity
import ru.mtuci.drivenext.presentation.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels {
        val useCase = ResolveStartDestinationUseCase(
            NetworkMonitor(applicationContext),
            OnboardingPreferences(applicationContext),
        )
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = SplashViewModel(useCase) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            // После заставки переходим на экран, выбранный ViewModel.
            delay(SPLASH_DURATION_MS)
            val target = when (viewModel.destination()) {
                StartDestination.NO_CONNECTION -> NoConnectionActivity::class.java
                StartDestination.ONBOARDING -> OnboardingActivity::class.java
                StartDestination.LOGIN -> LoginActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, target))
            finish()
        }
    }

    private companion object { const val SPLASH_DURATION_MS = 2_500L }
}
