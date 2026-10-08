package ru.mtuci.drivenext.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// ID картинки, заголовка и описания одной страницы.
data class OnboardingPage(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
)
