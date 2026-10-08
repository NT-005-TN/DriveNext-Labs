package ru.mtuci.drivenext.presentation.landlord
import android.content.Intent; import android.os.Bundle; import androidx.appcompat.app.AppCompatActivity; import ru.mtuci.drivenext.databinding.ActivityBecomeLandlordBinding
class BecomeLandlordActivity:AppCompatActivity(){override fun onCreate(s:Bundle?){super.onCreate(s);val b=ActivityBecomeLandlordBinding.inflate(layoutInflater);setContentView(b.root);b.backButton.setOnClickListener{finish()};b.startButton.setOnClickListener{startActivity(Intent(this,CarAddressActivity::class.java))}}}
