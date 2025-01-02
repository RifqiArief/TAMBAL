package com.example.kelompok2.DataModels

data class ServiceOrderModel(
    val orderId: String = "",
    val mechanicId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val status: String = "Pending",  // Pending, Accepted, Completed
    val timestamp: Long = System.currentTimeMillis()
)
