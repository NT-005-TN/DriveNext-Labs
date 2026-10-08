package ru.mtuci.drivenext.presentation.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mtuci.drivenext.data.cars.Car
import ru.mtuci.drivenext.databinding.ItemCarBinding

class CarAdapter(private val onDetails: (Car) -> Unit, private val onBook: (Car) -> Unit) : RecyclerView.Adapter<CarAdapter.Holder>() {
    private var items = emptyList<Car>()
    fun submitList(value: List<Car>) { items = value; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(private val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(car: Car) { binding.name.text = "${car.brand} ${car.model}"; binding.specs.text = car.specs; binding.price.text = "${car.pricePerDay} ₽ / день"; binding.detailsButton.setOnClickListener { onDetails(car) }; binding.bookButton.setOnClickListener { onBook(car) } }
    }
}
