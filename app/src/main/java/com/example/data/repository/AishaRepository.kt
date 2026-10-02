package com.example.data.repository

import com.example.data.db.AishaDao
import com.example.data.db.ChatMessageEntity
import com.example.data.db.MemoryEntity
import com.example.data.preferences.AishaPreferences
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.Mood
import com.example.model.VoiceMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AishaRepository(
    private val dao: AishaDao,
    val preferences: AishaPreferences
) {

    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()

    val chatMessages: Flow<List<ChatMessage>> = dao.getAllChatHistory().map { list ->
        list.map { entity ->
            ChatMessage(
                id = entity.id,
                sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.AISHA,
                text = entity.text,
                timestamp = entity.timestamp,
                mood = Mood.fromString(entity.mood),
                actionTaken = entity.actionTaken
            )
        }
    }

    suspend fun getMemoriesSnapshot(): List<MemoryEntity> {
        return dao.getMemoriesList()
    }

    suspend fun saveMessage(
        sender: MessageSender,
        text: String,
        mood: Mood,
        actionTaken: String? = null
    ): Long {
        val entity = ChatMessageEntity(
            sender = sender.name,
            text = text,
            timestamp = System.currentTimeMillis(),
            mood = mood.name,
            actionTaken = actionTaken
        )
        return dao.insertChatMessage(entity)
    }

    suspend fun addMemory(key: String, value: String, category: String = "General"): Long {
        return dao.insertMemory(MemoryEntity(key = key, value = value, category = category))
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteMemory(id)
    }

    suspend fun clearAllMemories() {
        dao.clearAllMemories()
    }

    suspend fun clearChatHistory() {
        dao.clearChatHistory()
    }

    fun setVoiceMode(mode: VoiceMode) = preferences.setVoiceMode(mode)
    fun getVoiceMode(): VoiceMode = preferences.getVoiceMode()
}
