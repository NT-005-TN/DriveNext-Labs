package ru.mtuci.drivenext.presentation.booking
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivityBookingSuccessBinding
import ru.mtuci.drivenext.presentation.main.MainActivity
class BookingSuccessActivity:AppCompatActivity(){override fun onCreate(state:Bundle?){super.onCreate(state);val b=ActivityBookingSuccessBinding.inflate(layoutInflater);setContentView(b.root);b.bookingsButton.setOnClickListener{Toast.makeText(this,"Активное бронирование: Toyota Camry",Toast.LENGTH_LONG).show()};b.homeButton.setOnClickListener{startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))}}}
