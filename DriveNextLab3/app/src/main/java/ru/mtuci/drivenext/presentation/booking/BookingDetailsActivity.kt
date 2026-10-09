package ru.mtuci.drivenext.presentation.booking
import android.widget.Button
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import java.time.LocalDate
import ru.mtuci.drivenext.presentation.common.*
class BookingDetailsActivity:WorkspaceActivity() {
    override fun onStart() { super.onStart();screen("Бронирование");work.run("booking",JSONObject().put("id",intent.getStringExtra("id"))) }
    override fun render(operation:String,value:JSONObject) {
        if(operation=="cancel") { finish();return }
        val body=screen("Бронирование")
        val active=value.getString("status")=="approved" && !LocalDate.parse(value.getString("end_date")).isBefore(LocalDate.now())
        body.label("Номер: "+value.getString("id")+"\n"+value.getString("car_name")+"\n"+value.getString("address")+
            "\n"+value.getString("start_date")+" — "+value.getString("end_date")+"\nВодитель: "+value.getString("driver_name")+
            "\nУдостоверение: "+value.getString("license_number")+"\nТариф: "+value.getInt("price_per_day")+" ₽/день"+
            "\nАренда: "+value.getLong("rental_total")+" ₽\nСтраховка: "+value.getLong("insurance_total")+
            " ₽\nИтого: "+value.getLong("total")+" ₽\nДепозит: "+value.getInt("deposit")+" ₽"+
            "\nСтатус: "+if(active) "Одобрено" else if(value.getString("status")=="cancelled") "Отменено" else "Завершено")
        if(active) body.addView(Button(this).apply { text="Отменить бронирование";setOnClickListener {
            MaterialAlertDialogBuilder(this@BookingDetailsActivity).setMessage("Отменить это бронирование?")
                .setPositiveButton("Да") { _,_->work.run("cancel",JSONObject().put("id",value.getString("id"))) }.setNegativeButton("Нет",null).show()
        } })
    }
}
