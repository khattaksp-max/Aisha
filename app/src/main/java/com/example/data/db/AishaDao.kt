package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AishaDao {

    // Memories
    @Query("SELECT * FROM aisha_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM aisha_memories ORDER BY timestamp DESC")
    suspend fun getMemoriesList(): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM aisha_memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM aisha_memories")
    suspend fun clearAllMemories()

    // Chat History
    @Query("SELECT * FROM chat_history ORDER BY id ASC")
    fun getAllChatHistory(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_history")
    suspend fun clearChatHistory()
}
