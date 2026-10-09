package ru.mtuci.drivenext.presentation.booking
import android.content.Intent
import android.os.Bundle
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.mtuci.drivenext.domain.RentalStartTime
import ru.mtuci.drivenext.databinding.ActivityBookingBinding
import ru.mtuci.drivenext.domain.RentalQuote
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
import ru.mtuci.drivenext.presentation.common.showDatePicker
class BookingActivity:WorkspaceActivity() {
    private lateinit var b:ActivityBookingBinding
    private var car:JSONObject?=null
    private var start:String=""
    private var end:String=""
    private var startTime:String=""
    private var requestId=UUID.randomUUID().toString()
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);b=ActivityBookingBinding.inflate(layoutInflater);setContentView(b.root)
        start=savedInstanceState?.getString("start").orEmpty(); end=savedInstanceState?.getString("end").orEmpty()
        startTime=savedInstanceState?.getString("start_time").orEmpty()
        requestId=savedInstanceState?.getString("request")?:requestId
        b.startDate.setOnClickListener { showDatePicker("Дата начала","yyyy-MM-dd") { start=it;requestId=UUID.randomUUID().toString();calculate() } }
        b.endDate.setOnClickListener { showDatePicker("Дата окончания","yyyy-MM-dd") { end=it;requestId=UUID.randomUUID().toString();calculate() } }
        b.startTime.setOnClickListener {
            val selected=runCatching { java.time.LocalTime.parse(startTime) }.getOrDefault(java.time.LocalTime.NOON)
            val picker=MaterialTimePicker.Builder().setTitleText("Время получения автомобиля")
                .setTimeFormat(TimeFormat.CLOCK_24H).setHour(selected.hour).setMinute(selected.minute).build()
            bindTimePicker(picker);picker.show(supportFragmentManager,"rental_start_time")
        }
        (supportFragmentManager.findFragmentByTag("rental_start_time") as? MaterialTimePicker)?.let(::bindTimePicker)
        b.continueButton.isEnabled=false
        b.continueButton.setOnClickListener {
            car?.let { work.run("book",JSONObject().put("request",requestId).put("id",it.getLong("id")).put("start",start).put("end",end).put("start_time",startTime)) }
        }
    }
    private fun bindTimePicker(picker:MaterialTimePicker) {
        picker.clearOnPositiveButtonClickListeners()
        picker.addOnPositiveButtonClickListener {
            startTime=String.format(java.util.Locale.ROOT,"%02d:%02d",picker.hour,picker.minute)
            requestId=UUID.randomUUID().toString();calculate()
        }
    }
    override fun onStart() { super.onStart();work.run("car",JSONObject().put("id",intent.getLongExtra("id",-1))) }
    private fun calculate() {
        b.startDate.text=start.ifBlank { "Дата начала" };b.endDate.text=end.ifBlank { "Дата окончания" }
        b.startTime.text=if(startTime.isBlank()) "Время начала" else "Время начала: "+startTime
        val c=car?:return
        val result=runCatching { RentalQuote.calculate(LocalDate.parse(start),LocalDate.parse(end),c.getInt("price_per_day"),c.getInt("insurance_per_day"),c.getInt("deposit")) }
        b.continueButton.isEnabled=result.isSuccess && RentalStartTime.valid(startTime)
        b.total.text=result.fold({ "Дней: "+it.days+"\nАренда: "+it.rental+" ₽\nСтраховка: "+it.insurance+" ₽\nИтого: "+it.total+" ₽\nВозвращаемый депозит: "+it.deposit+" ₽" },
            { if(start.isBlank()||end.isBlank()) "Выберите даты аренды" else "Проверьте даты: начало не раньше сегодня, окончание не раньше начала, не более 366 дней." })
    }
    override fun render(operation:String,value:JSONObject) {
        if(operation=="car") { car=value;b.carName.text=value.getString("brand")+" "+value.getString("model")+"\n"+value.getInt("price_per_day")+" ₽ / день";b.address.text=value.getString("address");calculate() }
        if(operation=="book") startActivity(Intent(this,BookingSuccessActivity::class.java).putExtra("id",value.getString("id")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
    override fun onSaveInstanceState(out:Bundle) { out.putString("start",start);out.putString("end",end);out.putString("start_time",startTime);out.putString("request",requestId);super.onSaveInstanceState(out) }
}
