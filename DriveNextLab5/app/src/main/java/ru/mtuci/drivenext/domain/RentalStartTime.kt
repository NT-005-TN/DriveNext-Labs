package ru.mtuci.drivenext.domain

import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Время получения автомобиля; посуточная стоимость по-прежнему считается по датам.
object RentalStartTime {
    fun valid(value:String):Boolean = Regex("\\d{2}:\\d{2}").matches(value) &&
        runCatching { LocalTime.parse(value) }.isSuccess
    fun display(value:String?):String = runCatching {
        LocalTime.parse(value).format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrDefault("время не указано")
}
