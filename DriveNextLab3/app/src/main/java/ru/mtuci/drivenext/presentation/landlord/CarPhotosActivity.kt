package ru.mtuci.drivenext.presentation.landlord
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONArray
import org.json.JSONObject
import ru.mtuci.drivenext.DriveNextApplication
import ru.mtuci.drivenext.data.cars.CarDraft
import ru.mtuci.drivenext.databinding.ActivityCarPhotosBinding
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
class CarPhotosActivity:WorkspaceActivity() {
 private lateinit var b:ActivityCarPhotosBinding
 private val app get()=application as DriveNextApplication
 private val photos=mutableListOf<String>()
 private val picker=registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()){items->
  if(items.isNotEmpty()){
   photos.clear()
   items.take(5).forEach{uri->runCatching{contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)};photos.add(uri.toString())}
   CarDraft.save(app,CarDraft.read(app).put("photos",JSONArray(photos)));renderPhotos()
   if(items.size>5) message("Выбраны первые пять фотографий — это максимальное количество.")
  }
 }
 override fun onCreate(savedInstanceState:Bundle?){
  super.onCreate(savedInstanceState);b=ActivityCarPhotosBinding.inflate(layoutInflater);setContentView(b.root)
  val saved=CarDraft.read(app).optJSONArray("photos")?:JSONArray()
  for(i in 0 until saved.length()) photos.add(saved.getString(i))
  b.backButton.setOnClickListener{finish()};b.chooseButton.setOnClickListener{picker.launch(arrayOf("image/jpeg","image/png","image/webp"))}
  b.nextButton.setOnClickListener{work.run("publishCar",CarDraft.read(app))}
  renderPhotos()
 }
 private fun renderPhotos(){
  b.photoStrip.removeAllViews()
  photos.forEach{uri->b.photoStrip.addView(ImageView(this).apply{runCatching{setImageURI(Uri.parse(uri))};scaleType=ImageView.ScaleType.CENTER_CROP},LinearLayout.LayoutParams(0,180,1f))}
  b.counter.text=photos.size.toString()+" / 5";b.nextButton.isEnabled=photos.isNotEmpty()
 }
 override fun render(operation:String,value:JSONObject){
  CarDraft.clear(app)
  startActivity(Intent(this,CarAddedActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
 }
}
