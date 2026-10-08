package ru.mtuci.drivenext.presentation.booking
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivityBookingBinding
import ru.mtuci.drivenext.presentation.common.showDatePicker
class BookingActivity:AppCompatActivity(){override fun onCreate(state:Bundle?){super.onCreate(state);val b=ActivityBookingBinding.inflate(layoutInflater);setContentView(b.root);b.carName.text=intent.getStringExtra("name")?:"Toyota Camry";b.startDate.setOnClickListener{showDatePicker("Дата начала","dd/MM/yyyy"){b.startDate.text=it}};b.endDate.setOnClickListener{showDatePicker("Дата окончания","dd/MM/yyyy"){b.endDate.text=it}};b.continueButton.setOnClickListener{startActivity(Intent(this,BookingSuccessActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))}}}
