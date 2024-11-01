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

class HomePopularCarsAdapter(private var popularCarList: List<CarModel>) : RecyclerView.Adapter<HomePopularCarsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modelTv: TextView = itemView.findViewById(R.id.homePopularCarModel)
        val seatsTv: TextView = itemView.findViewById(R.id.homePopularCarSeats)
        val doorsTv: TextView = itemView.findViewById(R.id.homePopularCarDoors)
        val transmissionTv: TextView = itemView.findViewById(R.id.homePopularCarTransmission)
        val addressTv: TextView = itemView.findViewById(R.id.homePopularCarAddress)
        val priceTv: TextView = itemView.findViewById(R.id.homePopularCarPrice)

        val carImage: ImageView = itemView.findViewById(R.id.homePopularCarImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomePopularCarsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_home_popular_car, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomePopularCarsAdapter.ViewHolder, position: Int) {
        val currentCar = popularCarList[position]

        holder.modelTv.text = currentCar.model
        holder.seatsTv.text = currentCar.seats.toString()
        holder.doorsTv.text = currentCar.doors.toString()
        holder.transmissionTv.text = currentCar.transmission
        holder.priceTv.text = "£${currentCar.servicePrice.toString()}"

        Picasso.get()
            .load(currentCar.image)
            .placeholder(R.drawable.ic_menu_gallery)
            .into(holder.carImage)

        holder.itemView.setOnClickListener{
            Toast.makeText(it.context, "${currentCar.brand} ${currentCar.model}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = popularCarList.size

    fun updateCarList(newCars: List<CarModel>) {
        popularCarList = newCars
        notifyDataSetChanged()
    }

}