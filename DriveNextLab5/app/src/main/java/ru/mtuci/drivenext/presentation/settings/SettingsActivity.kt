package ru.mtuci.drivenext.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
 override fun onCreate(state: Bundle?) { super.onCreate(state); val binding = ActivitySettingsBinding.inflate(layoutInflater); setContentView(binding.root); binding.profileRow.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }; binding.bookings.setOnClickListener { Toast.makeText(this, "Мои бронирования", Toast.LENGTH_SHORT).show() }; binding.connectCar.setOnClickListener { startActivity(Intent(this, ru.mtuci.drivenext.presentation.landlord.BecomeLandlordActivity::class.java)) } }
}
