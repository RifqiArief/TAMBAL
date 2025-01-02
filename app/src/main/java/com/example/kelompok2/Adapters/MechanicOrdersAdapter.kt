package com.example.kelompok2.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.DataModels.ServiceOrderModel
import com.example.kelompok2.databinding.ItemOrderBinding

class MechanicOrdersAdapter(
    private var ordersList: List<ServiceOrderModel>
) : RecyclerView.Adapter<MechanicOrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = ordersList.size

    fun updateOrderList(newList: List<ServiceOrderModel>) {
        ordersList = newList
        notifyDataSetChanged()
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: ServiceOrderModel) {
            binding.orderId.text = "Order ID: ${order.orderId}"
            binding.userName.text = "User: ${order.userName}"
            binding.status.text = "Status: ${order.status}"
        }
    }
}
