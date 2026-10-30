package ru.mtuci.drivenext.presentation.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

// Extension-функция сокращает подписку на изменение любого EditText.
fun EditText.afterTextChanged(block: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        // Полезное действие запускаем только после фактического изменения.
        override fun afterTextChanged(s: Editable?) = block()
    })
}
