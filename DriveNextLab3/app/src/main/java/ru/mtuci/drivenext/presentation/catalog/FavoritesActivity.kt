package ru.mtuci.drivenext.presentation.catalog
// Наследуем каталог, но загружаем локальные ID избранных машин.
class FavoritesActivity : ru.mtuci.drivenext.presentation.main.MainActivity() { override val favoritesMode = true }
