package ru.mtuci.drivenext.data.cars

import org.json.JSONArray
import org.json.JSONObject

internal data class PageWindow(val itemCount: Int, val hasMore: Boolean)

// Лишняя запись сообщает о наличии следующей страницы.
internal fun pageWindow(rawCount: Int, pageSize: Int) =
    PageWindow(itemCount = minOf(rawCount, pageSize), hasMore = rawCount > pageSize)

internal fun catalogPage(raw: JSONArray, page: Int, pageSize: Int): JSONObject {
    // Отдаём экрану только pageSize элементов и служебные поля пагинации.
    val window = pageWindow(raw.length(), pageSize)
    val items = JSONArray()
    for (index in 0 until window.itemCount) items.put(raw.getJSONObject(index))
    return JSONObject().put("items", items).put("page", page).put("has_more", window.hasMore)
}
