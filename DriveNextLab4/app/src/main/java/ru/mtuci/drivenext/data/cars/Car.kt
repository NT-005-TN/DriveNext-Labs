package ru.mtuci.drivenext.data.cars
import org.json.JSONObject
data class Car(val id: Long, val brand: String, val model: String, val pricePerDay: Int, val specs: String, val photo: String = "") {
    companion object {
        fun fromJson(j: JSONObject) = Car(j.getLong("id"), j.getString("brand"), j.getString("model"),
            j.getInt("price_per_day"), j.optInt("year").toString() + " · " + j.optString("transmission") + " · " + j.optInt("mileage") + " км",
            j.optJSONArray("photo_paths")?.optString(0).orEmpty())
    }
}
