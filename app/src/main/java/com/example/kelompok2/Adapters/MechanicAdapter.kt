package com.example.kelompok2.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.databinding.ItemMechanicBinding

class MechanicAdapter(
    private var mechanicList: List<CarModel>
) : RecyclerView.Adapter<MechanicAdapter.MechanicViewHolder>() {

    var onOrderClick: ((CarModel) -> Unit)? = null  // Callback for order button

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MechanicViewHolder {
        val binding = ItemMechanicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MechanicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MechanicViewHolder, position: Int) {
        val mechanic = mechanicList[position]
        holder.bind(mechanic)

        // Trigger the callback when Order button is clicked
        holder.binding.btnOrderService.setOnClickListener {
            onOrderClick?.invoke(mechanic)
        }
    }

    override fun getItemCount(): Int = mechanicList.size

    fun updateMechanicList(newList: List<CarModel>) {
        mechanicList = newList
        notifyDataSetChanged()
    }

    class MechanicViewHolder(val binding: ItemMechanicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mechanic: CarModel) {
            binding.mechanicName.text = mechanic.brand
            binding.mechanicLocation.text = mechanic.location
        }
    }
}
