package ru.mtuci.drivenext.presentation.catalog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.local.*
import ru.mtuci.drivenext.databinding.ActivityCarDetailsBinding
import ru.mtuci.drivenext.presentation.booking.BookingActivity
class CarDetailsActivity : AppCompatActivity() { override fun onCreate(state: Bundle?) { super.onCreate(state); val b=ActivityCarDetailsBinding.inflate(layoutInflater); setContentView(b.root); val id=intent.getLongExtra("id",1); val brand=intent.getStringExtra("brand")?:"Toyota"; val model=intent.getStringExtra("model")?:"Camry"; val price=intent.getIntExtra("price",4200); val specs=intent.getStringExtra("specs").orEmpty(); val dao=DriveNextDatabase.get(this).favorites(); fun render(){ b.favoriteButton.setText(if(dao.contains(id)) R.string.remove_favorite else R.string.add_favorite) }; b.carName.text="$brand $model"; b.carSpecs.text=specs; b.carPrice.text="$price ₽ / день"; render(); b.backButton.setOnClickListener{finish()}; b.favoriteButton.setOnClickListener{ if(dao.contains(id)) dao.delete(id) else dao.insert(FavoriteCarEntity(id,brand,model,price,specs)); render() }; b.bookButton.setOnClickListener{ startActivity(Intent(this, BookingActivity::class.java).putExtra("name","$brand $model")) } } }
