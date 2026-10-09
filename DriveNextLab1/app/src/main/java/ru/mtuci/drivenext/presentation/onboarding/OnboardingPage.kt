package ru.mtuci.drivenext.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPage(
    @DrawableRes val image: Int, // Хранит ID изображения страницы.
    @StringRes val title: Int, // Хранит ID строки заголовка.
    @StringRes val description: Int, // Хранит ID строки описания.
)
