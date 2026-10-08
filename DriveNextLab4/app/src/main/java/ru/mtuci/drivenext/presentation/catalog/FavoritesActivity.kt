package ru.mtuci.drivenext.presentation.catalog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.data.cars.Car
import ru.mtuci.drivenext.data.local.DriveNextDatabase
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.presentation.booking.BookingActivity
class FavoritesActivity : AppCompatActivity(){ override fun onCreate(state:Bundle?){super.onCreate(state);val b=ActivityMainBinding.inflate(layoutInflater);setContentView(b.root);b.title.text="Избранное";b.searchView.visibility=View.GONE;b.progress.visibility=View.GONE;b.bottomNavigation.visibility=View.GONE;val adapter=CarAdapter({openDetails(it)},{startActivity(Intent(this,BookingActivity::class.java).putExtra("name","${it.brand} ${it.model}"))});b.carList.layoutManager=LinearLayoutManager(this);b.carList.adapter=adapter;adapter.submitList(DriveNextDatabase.get(this).favorites().all().map{Car(it.id,it.brand,it.model,it.pricePerDay,it.specs)})} private fun openDetails(c:Car){startActivity(Intent(this,CarDetailsActivity::class.java).putExtra("id",c.id).putExtra("brand",c.brand).putExtra("model",c.model).putExtra("price",c.pricePerDay).putExtra("specs",c.specs))}}
