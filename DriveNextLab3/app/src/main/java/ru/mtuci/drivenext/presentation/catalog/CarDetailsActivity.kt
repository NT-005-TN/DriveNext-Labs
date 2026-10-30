package ru.mtuci.drivenext.presentation.catalog
import android.content.Intent
import android.os.Bundle
import org.json.JSONObject
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityCarDetailsBinding
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
import ru.mtuci.drivenext.presentation.common.fallbackCarImage
import ru.mtuci.drivenext.presentation.common.showPhoto
import ru.mtuci.drivenext.presentation.booking.BookingActivity
class CarDetailsActivity : WorkspaceActivity() {
    private lateinit var binding: ActivityCarDetailsBinding
    private var car: JSONObject? = null
    override fun onCreate(savedInstanceState:Bundle?) {
        // Настраиваем возврат, избранное и переход к оформлению аренды.
        super.onCreate(savedInstanceState); binding=ActivityCarDetailsBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }
        binding.favoriteButton.setOnClickListener { car?.let { work.run("toggleFavorite",it) } }
        binding.bookButton.setOnClickListener { car?.let { startActivity(Intent(this,BookingActivity::class.java).putExtra("id",it.getLong("id"))) } }
    }
    // Запрашиваем выбранную машину по id из Intent.
    override fun onStart() { super.onStart(); work.run("car",JSONObject().put("id",intent.getLongExtra("id",-1))) }
    override fun render(operation:String,value:JSONObject) {
        // Разделяем ответ машины и ответ локального состояния избранного.
        if(operation=="car") {
            car=value; binding.carName.text=value.getString("brand")+" "+value.getString("model")
            binding.carPhoto.setImageResource(fallbackCarImage(value.getString("brand"),value.getString("model")))
            showPhoto(binding.carPhoto,"car-photos",value.optJSONArray("photo_paths")?.optString(0).orEmpty())
            binding.carSpecs.text=value.optString("year")+" · "+value.optString("transmission")+"\nПробег: "+value.optString("mileage")+" км\n"+value.optString("description")+"\n"+value.optString("address")
            binding.carPrice.text=value.getInt("price_per_day").toString()+" ₽ / день"
            work.run("favoriteState",value)
        } else {
            val selected=value.optBoolean("favorite")
            binding.favoriteButton.setText(if(selected) R.string.remove_favorite else R.string.add_favorite)
            binding.favoriteButton.setTextColor(if(selected) android.graphics.Color.RED else getColor(R.color.purple_primary))
        }
    }
}
