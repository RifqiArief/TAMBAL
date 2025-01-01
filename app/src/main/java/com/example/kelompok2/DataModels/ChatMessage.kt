package com.example.kelompok2.DataModels

data class ChatMessage(
    val message: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
