package com.example.kelompok2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.R
import com.squareup.picasso.Picasso

class SearchCarsAdapter(private var carList: List<CarModel>) : RecyclerView.Adapter<SearchCarsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandTv: TextView = itemView.findViewById(R.id.sampleSearchCarBrand)
        val modelTv: TextView = itemView.findViewById(R.id.sampleSearchCarModel)
        val ratingTv: TextView = itemView.findViewById(R.id.sampleSearchCarRating)
        val addressTv: TextView = itemView.findViewById(R.id.sampleSearchCarAddress)

        val seatsTv: TextView = itemView.findViewById(R.id.sampleSearchCarSeats)
        val doorsTv: TextView = itemView.findViewById(R.id.sampleSearchCarDoors)
        val carTypeTv: TextView = itemView.findViewById(R.id.sampleSearchCarType)

        val carImage: ImageView = itemView.findViewById(R.id.sampleSearchCarImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchCarsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_search_car, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchCarsAdapter.ViewHolder, position: Int) {
        val currentCar = carList[position]

        holder.brandTv.text = currentCar.brand
        holder.modelTv.text = currentCar.model
        holder.ratingTv.text =currentCar.rating.toString()

        holder.seatsTv.text = currentCar.seats.toString()
        holder.doorsTv.text = currentCar.doors.toString()
        holder.carTypeTv.text = currentCar.carType

        Picasso.get()
            .load(currentCar.image)
            .placeholder(R.drawable.ic_menu_gallery)
            .into(holder.carImage)

        holder.itemView.setOnClickListener{
            Toast.makeText(it.context, "${currentCar.brand} ${currentCar.model}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = carList.size

    fun updateCarList(newCars: List<CarModel>) {
        carList = newCars
        notifyDataSetChanged()
    }
}