package ru.mtuci.drivenext.presentation.landlord
import android.content.Intent
import android.os.Bundle
import ru.mtuci.drivenext.DriveNextApplication
import ru.mtuci.drivenext.data.cars.CarDraft
import ru.mtuci.drivenext.databinding.ActivityCarAddressBinding
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.common.afterTextChanged
class CarAddressActivity:AuthActivity() {
 private lateinit var b:ActivityCarAddressBinding
 private val app get()=application as DriveNextApplication
 override fun onCreate(savedInstanceState:Bundle?) {
  // Восстанавливаем адрес из черновика и следим за заполнением поля.
  super.onCreate(savedInstanceState);b=ActivityCarAddressBinding.inflate(layoutInflater);setContentView(b.root)
  b.addressInput.setText(CarDraft.read(app).optString("address"));b.nextButton.isEnabled=b.addressInput.text?.isNotBlank()==true
  b.addressInput.afterTextChanged { b.nextButton.isEnabled=b.addressInput.text?.isNotBlank()==true }
  b.backButton.setOnClickListener { finish() }
  b.nextButton.setOnClickListener { save();startActivity(Intent(this,CarInfoActivity::class.java)) }
 }
 // Записываем очищенный адрес в общий черновик автомобиля.
 private fun save(){ CarDraft.save(app,CarDraft.read(app).put("address",b.addressInput.text.toString().trim())) }
 // Не теряем адрес при повороте или уходе с экрана.
 override fun onStop(){save();super.onStop()}
}
