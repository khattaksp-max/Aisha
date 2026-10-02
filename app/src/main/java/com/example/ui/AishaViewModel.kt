package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AishaApplication
import com.example.data.db.MemoryEntity
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.Mood
import com.example.model.VoiceMode
import com.example.model.VoiceState
import com.example.service.AishaVoiceForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AishaViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AishaApplication
    private val repository = app.repository
    private val preferences = app.preferences
    private val brain = app.brain
    private val phoneControl = app.phoneControlManager
    private val tts = app.ttsManager
    private val stt = app.speechRecognitionManager

    // UI States
    private val _currentMood = MutableStateFlow(Mood.HAPPY)
    val currentMood: StateFlow<Mood> = _currentMood.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    val isListening: StateFlow<Boolean> = stt.isListening
    val isSpeaking: StateFlow<Boolean> = tts.isSpeaking
    val liveRmsDb: StateFlow<Float> = stt.rmsDb
    val speechAmplitude: StateFlow<Float> = tts.speechAmplitude

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceMode: StateFlow<VoiceMode> = preferences.voiceModeFlow
    val cuteLevel: StateFlow<Float> = preferences.cuteLevelFlow
    val speakingSpeed: StateFlow<Float> = preferences.speedFlow
    val voicePitch: StateFlow<Float> = preferences.pitchFlow
    val bgVoiceEnabled: StateFlow<Boolean> = preferences.bgVoiceFlow
    val memoryEnabled: StateFlow<Boolean> = preferences.memoryEnabledFlow

    private val _lastActionFeedback = MutableStateFlow<String?>(null)
    val lastActionFeedback: StateFlow<String?> = _lastActionFeedback.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userTranscript = MutableStateFlow("")
    val userTranscript: StateFlow<String> = _userTranscript.asStateFlow()

    init {
        // Collect speaking state to sync voice state
        viewModelScope.launch {
            tts.isSpeaking.collect { speaking ->
                if (speaking) {
                    _voiceState.value = VoiceState.SPEAKING
                } else if (_voiceState.value == VoiceState.SPEAKING) {
                    _voiceState.value = VoiceState.IDLE
                }
            }
        }
    }

    /**
     * Start speech recognition
     */
    fun startListening() {
        // Barge-in: if Aisha is speaking, stop immediately!
        if (tts.isSpeaking.value) {
            tts.stop()
        }

        _voiceState.value = VoiceState.LISTENING
        _errorMessage.value = null
        _userTranscript.value = ""

        stt.startListening(
            onResult = { recognizedText ->
                _voiceState.value = VoiceState.THINKING
                _userTranscript.value = recognizedText
                processUserInput(recognizedText)
            },
            onError = { err ->
                _voiceState.value = VoiceState.IDLE
                _errorMessage.value = err
            }
        )
    }

    fun stopListening() {
        stt.stopListening()
        if (_voiceState.value == VoiceState.LISTENING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    /**
     * Barge-in interruption: immediately halts voice output or input
     */
    fun interruptAndStop() {
        tts.stop()
        stt.cancel()
        _voiceState.value = VoiceState.IDLE
    }

    fun sendTextMessage(text: String) {
        if (text.isBlank()) return
        interruptAndStop()
        _voiceState.value = VoiceState.THINKING
        _userTranscript.value = text
        processUserInput(text)
    }

    private fun processUserInput(input: String) {
        viewModelScope.launch {
            // 1. Save user chat message
            repository.saveMessage(MessageSender.USER, input, _currentMood.value)

            // 2. Fetch history and memories
            val recentTurns = chatMessages.value.takeLast(6).map {
                (if (it.sender == MessageSender.USER) "USER" else "AISHA") to it.text
            }
            val memoryList = if (preferences.isMemoryEnabled()) repository.getMemoriesSnapshot() else emptyList()

            // 3. AISHA decides (Gemini + Emotional Mood Engine)
            val decision = brain.think(input, recentTurns, memoryList)

            // 4. Update Mood
            _currentMood.value = decision.mood

            // 5. Store memory if learned
            if (preferences.isMemoryEnabled() && decision.newMemory != null) {
                repository.addMemory(decision.newMemory.first, decision.newMemory.second, "Learned")
            }

            // 6. Execute phone action if detected
            var actionResultSummary: String? = null
            if (decision.detectedAction != null) {
                val actionResult = phoneControl.executeAction(decision.detectedAction)
                if (actionResult != null) {
                    actionResultSummary = actionResult.summary
                    _lastActionFeedback.value = actionResult.summary
                }
            } else {
                // Check if user input directly requested a phone action
                val actionResult = phoneControl.executeAction(input)
                if (actionResult != null) {
                    actionResultSummary = actionResult.summary
                    _lastActionFeedback.value = actionResult.summary
                }
            }

            // 7. Save Aisha response to chat history
            repository.saveMessage(
                MessageSender.AISHA,
                decision.spokenText,
                decision.mood,
                actionResultSummary
            )

            // 8. Speak response naturally
            _voiceState.value = VoiceState.SPEAKING
            tts.speak(decision.spokenText, decision.mood) {
                _voiceState.value = VoiceState.IDLE
            }
        }
    }

    fun replayMessage(msg: ChatMessage) {
        if (msg.sender == MessageSender.AISHA) {
            tts.stop()
            _currentMood.value = msg.mood
            _voiceState.value = VoiceState.SPEAKING
            tts.speak(msg.text, msg.mood) {
                _voiceState.value = VoiceState.IDLE
            }
        }
    }

    fun toggleBackgroundVoice() {
        val newState = !preferences.isBackgroundVoiceEnabled()
        preferences.setBackgroundVoiceEnabled(newState)
        if (newState) {
            AishaVoiceForegroundService.startService(app)
        } else {
            AishaVoiceForegroundService.stopService(app)
        }
    }

    fun setVoiceMode(mode: VoiceMode) {
        preferences.setVoiceMode(mode)
    }

    fun previewVoice(mode: VoiceMode) {
        tts.previewVoice(mode)
    }

    fun setSpeakingSpeed(speed: Float) = preferences.setSpeakingSpeed(speed)
    fun setVoicePitch(pitch: Float) = preferences.setVoicePitch(pitch)
    fun setVoiceVolume(vol: Float) = preferences.setVoiceVolume(vol)
    fun setCuteLevel(cute: Float) = preferences.setCuteLevel(cute)
    fun setEmotionalExpression(expr: Float) = preferences.setEmotionalExpression(expr)
    fun setCaringLevel(level: Float) = preferences.setCaringLevel(level)
    fun setPlayfulnessLevel(level: Float) = preferences.setPlayfulnessLevel(level)
    fun setAffectionLevel(level: Float) = preferences.setAffectionLevel(level)
    fun setJealousyLevel(level: Float) = preferences.setJealousyLevel(level)
    fun setMemoryEnabled(enabled: Boolean) = preferences.setMemoryEnabled(enabled)
    fun setUserName(name: String) = preferences.setUserName(name)

    fun addManualMemory(key: String, value: String, category: String) {
        viewModelScope.launch {
            repository.addMemory(key, value, category)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearMemories() {
        viewModelScope.launch {
            repository.clearAllMemories()
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    fun dismissActionFeedback() {
        _lastActionFeedback.value = null
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun completeOnboarding() {
        preferences.setOnboardingCompleted(true)
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
        stt.destroy()
    }
}
