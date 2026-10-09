package ru.mtuci.drivenext.presentation.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.data.cars.Car
import ru.mtuci.drivenext.databinding.ItemCarBinding
import ru.mtuci.drivenext.presentation.common.WorkspaceActivity
import ru.mtuci.drivenext.presentation.common.showPhoto

class CarAdapter(private val onDetails:(Car)->Unit, private val onBook:(Car)->Unit):RecyclerView.Adapter<CarAdapter.Holder>() {
    private var items=emptyList<Car>()
    fun submitList(value:List<Car>) { items=value;notifyDataSetChanged() }
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int)=Holder(ItemCarBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    override fun getItemCount()=items.size
    override fun onBindViewHolder(holder:Holder,position:Int)=holder.bind(items[position])
    inner class Holder(private val b:ItemCarBinding):RecyclerView.ViewHolder(b.root) {
        fun bind(car:Car) {
            b.name.text=car.brand+" "+car.model;b.specs.text=car.specs;b.price.text=car.pricePerDay.toString()+" ₽ / день"
            b.carPhoto.tag=null;b.carPhoto.setImageResource(R.drawable.splash_art)
            (b.root.context as? WorkspaceActivity)?.showPhoto(b.carPhoto,"car-photos",car.photo)
            b.detailsButton.setOnClickListener { onDetails(car) };b.bookButton.setOnClickListener { onBook(car) }
        }
    }
}
