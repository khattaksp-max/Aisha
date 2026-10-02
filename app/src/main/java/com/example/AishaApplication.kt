package com.example

import android.app.Application
import com.example.ai.AishaBrain
import com.example.data.db.AishaDatabase
import com.example.data.preferences.AishaPreferences
import com.example.data.repository.AishaRepository
import com.example.system.PhoneControlManager
import com.example.voice.SpeechRecognitionManager
import com.example.voice.TextToSpeechManager

class AishaApplication : Application() {

    lateinit var database: AishaDatabase private set
    lateinit var preferences: AishaPreferences private set
    lateinit var repository: AishaRepository private set
    lateinit var phoneControlManager: PhoneControlManager private set
    lateinit var ttsManager: TextToSpeechManager private set
    lateinit var speechRecognitionManager: SpeechRecognitionManager private set
    lateinit var brain: AishaBrain private set

    override fun onCreate() {
        super.onCreate()
        database = AishaDatabase.getInstance(this)
        preferences = AishaPreferences(this)
        repository = AishaRepository(database.aishaDao(), preferences)
        phoneControlManager = PhoneControlManager(this)
        ttsManager = TextToSpeechManager(this, preferences)
        speechRecognitionManager = SpeechRecognitionManager(this)
        brain = AishaBrain(preferences)
    }
}
