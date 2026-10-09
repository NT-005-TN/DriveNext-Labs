package ru.mtuci.drivenext.presentation.booking
import android.content.Intent
import android.widget.Button
import org.json.JSONObject
import java.time.LocalDate
import ru.mtuci.drivenext.presentation.common.*
class BookingsActivity:WorkspaceActivity() {
    override fun onStart() { super.onStart();screen("Мои бронирования");work.run("bookings") }
    override fun render(operation:String,value:JSONObject) {
        val body=screen("Мои бронирования");val rows=value.getJSONArray("items")
        if(rows.length()==0) body.label("Бронирований пока нет")
        for(i in 0 until rows.length()) {
            val row=rows.getJSONObject(i)
            val status=if(row.getString("status")=="cancelled") "Отменено" else if(LocalDate.parse(row.getString("end_date")).isBefore(LocalDate.now())) "Завершено" else "Активно"
            body.addView(Button(this).apply {
                text=row.getString("car_name")+"\n"+row.getString("start_date")+" — "+row.getString("end_date")+"\n"+status
                setOnClickListener { startActivity(Intent(this@BookingsActivity,BookingDetailsActivity::class.java).putExtra("id",row.getString("id"))) }
            })
        }
    }
}
