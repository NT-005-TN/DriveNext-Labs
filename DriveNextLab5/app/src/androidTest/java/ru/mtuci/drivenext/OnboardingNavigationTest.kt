package ru.mtuci.drivenext

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.mtuci.drivenext.data.preferences.OnboardingPreferences
import ru.mtuci.drivenext.presentation.onboarding.OnboardingActivity
import ru.mtuci.drivenext.presentation.splash.SplashActivity

@RunWith(AndroidJUnit4::class)
class OnboardingNavigationTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    private fun withFreshOnboarding(block: () -> Unit) {
        val preferences = context.getSharedPreferences("drive_next_preferences", 0)
        val hadValue = preferences.contains("onboarding_completed")
        val previous = preferences.getBoolean("onboarding_completed", false)
        preferences.edit().remove("onboarding_completed").commit()
        try { block() } finally {
            if (hadValue) preferences.edit().putBoolean("onboarding_completed", previous).commit()
            else preferences.edit().remove("onboarding_completed").commit()
        }
    }

    private fun onResumed(block: (Activity) -> Unit) {
        instrumentation.waitForIdleSync()
        instrumentation.runOnMainSync {
            val activity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED).single()
            block(activity)
        }
    }

    // Вызываем реальный обработчик; инжектор Espresso 3.6 несовместим с Android 17.
    private fun click(id: Int) {
        onResumed { assertTrue(it.findViewById<View>(id).performClick()) }
        Thread.sleep(600)
        instrumentation.waitForIdleSync()
    }

    @Test fun launcherIsSplash() {
        val launcher = context.packageManager.getLaunchIntentForPackage(context.packageName)
        assertEquals(SplashActivity::class.java.name, launcher?.component?.className)
    }

    @Test fun firstLaunchShowsPagesUntilUserFinishes() = withFreshOnboarding {
        ActivityScenario.launch<SplashActivity>(
            Intent(context, SplashActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ).use {
            Thread.sleep(3200)
            onResumed { activity ->
                assertTrue(activity is OnboardingActivity)
                assertTrue(activity.findViewById<View>(R.id.viewPager).isShown)
            }
            assertFalse(OnboardingPreferences(context).isCompleted())
            click(R.id.nextButton)
            click(R.id.nextButton)
            onResumed { activity ->
                assertEquals(2, activity.findViewById<ViewPager2>(R.id.viewPager).currentItem)
                assertEquals(context.getString(R.string.start), activity.findViewById<TextView>(R.id.nextButton).text.toString())
            }
            assertFalse(OnboardingPreferences(context).isCompleted())
            click(R.id.nextButton)
            assertTrue(OnboardingPreferences(context).isCompleted())
            onResumed { activity -> assertFalse(activity is OnboardingActivity) }
        }
    }

    @Test fun rotationKeepsPageWithoutCompletingOnboarding() = withFreshOnboarding {
        ActivityScenario.launch(OnboardingActivity::class.java).use { scenario ->
            click(R.id.nextButton)
            scenario.recreate()
            scenario.onActivity {
                assertEquals(1, it.findViewById<ViewPager2>(R.id.viewPager).currentItem)
                assertFalse(OnboardingPreferences(context).isCompleted())
            }
        }
    }

    @Test fun skipCompletesOnboarding() = withFreshOnboarding {
        ActivityScenario.launch(OnboardingActivity::class.java).use {
            click(R.id.skipButton)
            assertTrue(OnboardingPreferences(context).isCompleted())
            onResumed { activity -> assertFalse(activity is OnboardingActivity) }
        }
    }
}
