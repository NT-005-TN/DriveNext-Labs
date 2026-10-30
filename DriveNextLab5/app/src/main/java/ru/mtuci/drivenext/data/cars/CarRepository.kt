package ru.mtuci.drivenext.data.cars

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import ru.mtuci.drivenext.data.auth.SupabaseAuthRepository
import ru.mtuci.drivenext.data.local.DriveNextDatabase
import ru.mtuci.drivenext.data.local.FavoriteCarEntity
import ru.mtuci.drivenext.domain.AuthException
import java.time.LocalDate

// Все операции каталога, профиля и аренды используют настоящую серверную сессию.
class CarRepository(private val context: Context, private val api: SupabaseAuthRepository) {
    // Приводим серверный массив к единому формату результата ViewModel.
    private fun items(raw: String) = JSONObject().put("items", JSONArray(raw))
    // Получаем первую строку или выдаём понятную ошибку доступа.
    private fun first(raw: String): JSONObject = JSONArray(raw).optJSONObject(0)
        ?: throw AuthException("Запись не найдена или недоступна.")
    private suspend fun profile(): JSONObject {
        // Сначала определяем UUID текущего пользователя, затем читаем profiles.
        val user = JSONObject(api.call("/auth/v1/user"))
        val rows = JSONArray(api.call("/rest/v1/profiles?id=eq." + user.getString("id")))
        return (rows.optJSONObject(0) ?: JSONObject().put("id", user.getString("id"))
            .put("name", user.optJSONObject("user_metadata")?.optString("full_name").orEmpty()))
            .put("email", user.optString("email"))
    }
    // Выбираем серверную или локальную операцию по её строковому имени.
    suspend fun execute(operation: String, p: JSONObject): JSONObject = when (operation) {
        "cars" -> {
            // Готовим безопасный фильтр, offset и limit для каталога.
            val query = p.optString("query").trim()
            val page = p.optInt("page", 0).coerceAtLeast(0)
            val pageSize = p.optInt("page_size", 10).coerceIn(1, 50)
            val offset = page * pageSize
            // Спецсимволы PostgREST не попадают в выражение фильтра.
            val words = query.split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotBlank() }
            val filters = words.joinToString("") { "&or=" + Uri.encode("(brand.ilike.*" + it + "*,model.ilike.*" + it + "*)") }
            // Берём на одну запись больше, чтобы точно понять, есть ли следующая страница.
            val raw = JSONArray(api.call("/rest/v1/cars?active=eq.true&order=id.desc" + filters +
                "&offset=" + offset + "&limit=" + (pageSize + 1)))
            catalogPage(raw, page, pageSize)
        }
        "car" -> first(api.call("/rest/v1/cars?id=eq." + p.getLong("id")))
        "profile" -> profile()
        "saveProfile" -> {
            // Загружаем новый аватар и обновляем профиль через upsert.
            val id = api.ownerId()
            val payload = JSONObject().put("id", id).put("name", p.optString("name").trim())
                .put("surname", p.optString("surname").trim()).put("license_number", p.optString("license").trim().ifBlank { null } ?: JSONObject.NULL)
            if (p.optString("avatar").isNotBlank()) payload.put("profile_photo", api.photo("registration-documents", "avatar", p.getString("avatar")))
            api.call("/rest/v1/profiles?on_conflict=id", "POST", payload)
            profile()
        }
        "book" -> JSONObject(api.call("/rest/v1/rpc/create_booking_with_time", "POST",
            JSONObject().put("p_request", p.getString("request")).put("p_car", p.getLong("id"))
                .put("p_start", p.getString("start")).put("p_end", p.getString("end")).put("p_start_time",p.getString("start_time"))))
        "bookings" -> items(api.call("/rest/v1/bookings?order=created_at.desc"))
        "booking" -> first(api.call("/rest/v1/bookings?id=eq." + Uri.encode(p.getString("id"))))
        "cancel" -> {
            api.call("/rest/v1/rpc/cancel_booking", "POST", JSONObject().put("p_id", p.getString("id")))
            execute("booking", p)
        }
        "favorites" -> {
            // Локальные ID из Room превращаем в серверный запрос автомобилей.
            val dao = DriveNextDatabase.get(context, api.ownerId()).favorites()
            val ids = dao.all().map { it.id }
            if (ids.isEmpty()) JSONObject().put("items", JSONArray())
            else items(api.call("/rest/v1/cars?active=eq.true&id=in.(" + ids.joinToString(",") + ")"))
        }
        "favoriteState" -> JSONObject().put("favorite", DriveNextDatabase.get(context, api.ownerId()).favorites().contains(p.getLong("id")))
        "toggleFavorite" -> {
            // Добавляем или удаляем одну машину из пользовательской базы Room.
            val c = Car.fromJson(p)
            val dao = DriveNextDatabase.get(context, api.ownerId()).favorites()
            if (dao.contains(c.id)) dao.delete(c.id) else dao.insert(FavoriteCarEntity(c.id,c.brand,c.model,c.pricePerDay,c.specs))
            JSONObject().put("favorite", dao.contains(c.id))
        }
        "saveCar" -> {
            // Проверяем черновик и создаём неактивную серверную запись.
            val year = p.optString("year").toIntOrNull() ?: 0
            val mileage = p.optString("mileage").toIntOrNull() ?: -1
            if (year !in 1900..LocalDate.now().year || mileage < 0) throw AuthException("Проверьте год выпуска и пробег.")
            for (key in listOf("brand","model","address","transmission","description"))
                if (p.optString(key).isBlank()) throw AuthException("Заполните все поля автомобиля.")
            val payload = JSONObject(p.toString()).put("owner_id",api.ownerId()).put("year",year).put("mileage",mileage)
            payload.remove("photos"); payload.remove("id")
            // Повторная отправка обновляет ту же запись по client_id.
            api.call("/rest/v1/cars?on_conflict=client_id", "POST", payload)
            first(api.call("/rest/v1/cars?client_id=eq." + Uri.encode(p.getString("client_id"))))
        }
        "publishCar" -> {
            // После загрузки 1–5 фото делаем автомобиль видимым в каталоге.
            val uris = p.getJSONArray("photos")
            if (uris.length() !in 1..5) throw AuthException("Выберите от 1 до 5 фотографий.")
            val paths = JSONArray()
            for (i in 0 until uris.length()) paths.put(api.photo("car-photos",p.getLong("id").toString()+"/"+i,uris.getString(i)))
            api.call("/rest/v1/cars?id=eq."+p.getLong("id"),"PATCH",JSONObject().put("photo_paths",paths).put("active",true))
            JSONObject().put("id",p.getLong("id"))
        }
        else -> throw AuthException("Неизвестная операция.")
    }
}
