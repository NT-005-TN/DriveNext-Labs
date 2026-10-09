package ru.mtuci.drivenext.presentation.landlord
import android.content.Intent
import android.os.Bundle
import android.widget.*
import org.json.JSONObject
import ru.mtuci.drivenext.DriveNextApplication
import ru.mtuci.drivenext.data.cars.CarDraft
import ru.mtuci.drivenext.databinding.ActivityCarInfoBinding
import ru.mtuci.drivenext.presentation.common.*
class CarInfoActivity:WorkspaceActivity() {
 private lateinit var b:ActivityCarInfoBinding
 private val app get()=application as DriveNextApplication
 private val types=listOf("Выберите трансмиссию","Автоматическая","Механическая","Робот","Вариатор")
 override fun onCreate(savedInstanceState:Bundle?) {
  super.onCreate(savedInstanceState);b=ActivityCarInfoBinding.inflate(layoutInflater);setContentView(b.root)
  val draft=CarDraft.read(app)
  b.transmissionSpinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,types)
  b.yearInput.setText(draft.optString("year"));b.brandInput.setText(draft.optString("brand"));b.modelInput.setText(draft.optString("model"))
  b.mileageInput.setText(draft.optString("mileage"));b.descriptionInput.setText(draft.optString("description"))
  b.transmissionSpinner.setSelection(types.indexOf(draft.optString("transmission")).coerceAtLeast(0))
  fun update(){ b.sendButton.isEnabled=listOf(b.yearInput,b.brandInput,b.modelInput,b.mileageInput,b.descriptionInput).all{it.text.isNotBlank()}&&b.transmissionSpinner.selectedItemPosition>0 }
  listOf(b.yearInput,b.brandInput,b.modelInput,b.mileageInput,b.descriptionInput).forEach{it.afterTextChanged(::update)}
  b.transmissionSpinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener {
   override fun onItemSelected(p:AdapterView<*>?,v:android.view.View?,pos:Int,id:Long)=update()
   override fun onNothingSelected(p:AdapterView<*>?)=Unit
  }
  b.backButton.setOnClickListener{finish()}
  b.sendButton.setOnClickListener{save();work.run("saveCar",CarDraft.read(app))}
  update()
 }
 private fun save(){
  val d=CarDraft.read(app).put("year",b.yearInput.text.toString()).put("brand",b.brandInput.text.toString().trim()).put("model",b.modelInput.text.toString().trim())
   .put("mileage",b.mileageInput.text.toString()).put("description",b.descriptionInput.text.toString().trim()).put("transmission",b.transmissionSpinner.selectedItem.toString())
  CarDraft.save(app,d)
 }
 override fun onStop(){save();super.onStop()}
 override fun render(operation:String,value:JSONObject) {
  CarDraft.save(app,CarDraft.read(app).put("id",value.getLong("id")))
  startActivity(Intent(this,CarPhotosActivity::class.java))
 }
}
