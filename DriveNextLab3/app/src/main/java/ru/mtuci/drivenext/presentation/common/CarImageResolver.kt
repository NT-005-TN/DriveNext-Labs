package ru.mtuci.drivenext.presentation.common

import androidx.annotation.DrawableRes
import ru.mtuci.drivenext.R

// Показываем фото из APK, если для учебной машины в Storage ещё нет файла.
@DrawableRes
fun fallbackCarImage(brand: String, model: String): Int = when {
    brand.equals("Toyota", true) && model.contains("Camry", true) -> R.drawable.car_toyota_camry
    brand.equals("BMW", true) && model.contains("X3", true) -> R.drawable.car_bmw_x3
    brand.equals("Kia", true) && model.contains("K5", true) -> R.drawable.car_kia_k5
    brand.equals("Tesla", true) && model.contains("Model 3", true) -> R.drawable.car_tesla_model3
    brand.equals("Volkswagen", true) && model.contains("Polo", true) -> R.drawable.car_volkswagen_polo
    else -> R.drawable.splash_art
}
