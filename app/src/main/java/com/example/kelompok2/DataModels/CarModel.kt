package com.example.kelompok2.DataModels

data class CarModel(
    val brand: String,
    val model: String,
    val seats: Int,
    val doors: Int,
    val transmission: String,
    val rating: Double,
    val carType: String,
    val image: String,
    val servicePrice: Int,
    val location: String? = null // Add location as nullable
)
