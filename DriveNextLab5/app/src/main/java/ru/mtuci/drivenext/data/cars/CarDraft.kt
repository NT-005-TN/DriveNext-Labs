package ru.mtuci.drivenext.data.cars
import org.json.JSONObject
import java.util.UUID
import ru.mtuci.drivenext.DriveNextApplication
object CarDraft {
    // Читаем сохранённый JSON или создаём новый стабильный client_id.
    fun read(app:DriveNextApplication):JSONObject {
        val existing=app.secureStore.get("car_draft")
        return if(existing!=null) JSONObject(existing) else JSONObject().put("client_id",UUID.randomUUID().toString())
    }
    // Шифруем актуальный черновик через общий SecureStore.
    fun save(app:DriveNextApplication,value:JSONObject) { app.secureStore.put("car_draft",value.toString()) }
    // Удаляем черновик только после успешной публикации.
    fun clear(app:DriveNextApplication) { app.secureStore.remove("car_draft") }
}
