package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "AISHA"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String = "HAPPY",
    val actionTaken: String? = null
)
