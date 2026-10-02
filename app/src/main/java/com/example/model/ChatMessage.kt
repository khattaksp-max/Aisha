package com.example.model

enum class MessageSender {
    USER,
    AISHA
}

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: Mood = Mood.HAPPY,
    val actionTaken: String? = null
)
