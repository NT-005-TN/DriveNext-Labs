package ru.mtuci.drivenext.presentation.booking
import android.content.Intent
import android.os.Bundle
import ru.mtuci.drivenext.databinding.ActivityBookingSuccessBinding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.main.MainActivity
class BookingSuccessActivity:AuthActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);val b=ActivityBookingSuccessBinding.inflate(layoutInflater);setContentView(b.root)
        b.bookingsButton.setOnClickListener { startActivity(Intent(this,BookingsActivity::class.java)) }
        b.homeButton.setOnClickListener { navigate(MainActivity::class.java,true) }
    }
}
