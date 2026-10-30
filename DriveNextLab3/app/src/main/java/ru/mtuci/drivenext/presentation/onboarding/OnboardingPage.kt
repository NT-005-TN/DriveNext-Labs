package ru.mtuci.drivenext.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// Модель хранит идентификаторы ресурсов одной страницы онбординга.
data class OnboardingPage(
    // Аннотации не дают случайно передать ресурс неправильного типа.
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
)
