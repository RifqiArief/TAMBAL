package com.example.kelompok2.DataModels

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null  // Tambahkan imageUrl sebagai opsional
)