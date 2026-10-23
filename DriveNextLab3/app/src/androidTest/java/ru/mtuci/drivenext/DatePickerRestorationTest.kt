package ru.mtuci.drivenext

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.datepicker.MaterialDatePicker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.mtuci.drivenext.data.registration.RegistrationDraft as Draft
import ru.mtuci.drivenext.presentation.registration.RegisterStep2Activity

@RunWith(AndroidJUnit4::class)
class DatePickerRestorationTest {
    @Test fun restoredCalendarUpdatesNewScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Инициализируем Application до чтения черновика.
        context.applicationContext
        val previous = Draft.birthDate
        try {
            ActivityScenario.launch(RegisterStep2Activity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    MaterialDatePicker.Builder.datePicker()
                        .setSelection(946684800000L) // 01.01.2000 UTC
                        .build().showNow(activity.supportFragmentManager, "date_picker_" + activity.getString(R.string.birth_date))
                }
                scenario.recreate()
                scenario.onActivity { activity ->
                    val picker = activity.supportFragmentManager
                        .findFragmentByTag("date_picker_" + activity.getString(R.string.birth_date)) as MaterialDatePicker<*>
                    assertTrue(picker.requireDialog().findViewById<android.view.View>(
                        com.google.android.material.R.id.confirm_button).performClick())
                    assertEquals("01/01/2000", activity.findViewById<EditText>(R.id.birthDateInput).text.toString())
                }
            }
        } finally {
            Draft.birthDate = previous
            Draft.save()
        }
    }
}
