package ru.mtuci.drivenext.presentation.common

import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun FragmentActivity.showDatePicker(title: String, pattern: String, onSelected: (String) -> Unit) {
    // Общий календарь используется для даты рождения и даты выдачи документа.
    val picker = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
    picker.addOnPositiveButtonClickListener { millis ->
        // UTC не даёт выбранной дате сместиться из-за часового пояса устройства.
        val formatter = SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        onSelected(formatter.format(Date(millis)))
    }
    picker.show(supportFragmentManager, "date_picker")
}
