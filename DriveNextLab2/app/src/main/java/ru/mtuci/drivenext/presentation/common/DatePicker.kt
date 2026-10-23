package ru.mtuci.drivenext.presentation.common

import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun bindDatePicker(picker: MaterialDatePicker<Long>, pattern: String, onSelected: (String) -> Unit) {
    picker.clearOnPositiveButtonClickListeners()
    picker.addOnPositiveButtonClickListener { millis ->
        val formatter = SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        onSelected(formatter.format(Date(millis)))
    }
}

// После поворота календарь восстанавливается, но его обработчик нужно привязать заново.
@Suppress("UNCHECKED_CAST")
fun FragmentActivity.restoreDatePicker(title: String, pattern: String, onSelected: (String) -> Unit) {
    (supportFragmentManager.findFragmentByTag("date_picker_$title") as? MaterialDatePicker<Long>)
        ?.let { bindDatePicker(it, pattern, onSelected) }
}

fun FragmentActivity.showDatePicker(title: String, pattern: String, onSelected: (String) -> Unit) {
    val tag = "date_picker_$title"
    if (supportFragmentManager.findFragmentByTag(tag) != null) return
    val picker = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
    bindDatePicker(picker, pattern, onSelected)
    picker.show(supportFragmentManager, tag)
}
