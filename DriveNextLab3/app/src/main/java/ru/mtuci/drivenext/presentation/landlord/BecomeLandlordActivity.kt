package ru.mtuci.drivenext.presentation.landlord
import android.content.Intent
import android.os.Bundle
import ru.mtuci.drivenext.databinding.ActivityBecomeLandlordBinding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.settings.SettingsActivity
class BecomeLandlordActivity:AuthActivity() {
 override fun onCreate(savedInstanceState:Bundle?) { super.onCreate(savedInstanceState);val b=ActivityBecomeLandlordBinding.inflate(layoutInflater);setContentView(b.root)
 b.backButton.setOnClickListener { startActivity(Intent(this,SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish() }
 b.startButton.setOnClickListener { startActivity(Intent(this,CarAddressActivity::class.java)) } }
}
