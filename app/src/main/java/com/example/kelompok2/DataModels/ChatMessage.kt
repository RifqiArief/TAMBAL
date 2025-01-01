package com.example.kelompok2.DataModels

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var isUser: Boolean = false  // Tambahkan ini
)