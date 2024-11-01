package com.example.kelompok2.DataModels

import java.util.Date

data class ServiceModel(
    val serviceId: String = "n/a",
    val userId: String = "n/a",
    val carId: String = "n/a",
    val serviceDate: Date = Date(946684800),
    val expectedReturnDate: Date = Date(946684800),
    val actualReturnDate: Date = Date(946684800)
)
