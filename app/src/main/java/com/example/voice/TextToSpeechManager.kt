package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.preferences.AishaPreferences
import com.example.model.Mood
import com.example.model.VoiceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

class TextToSpeechManager(
    private val context: Context,
    private val preferences: AishaPreferences
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Throwable) {
            Log.e("TextToSpeechManager", "Failed to initialize TextToSpeech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            selectBestFeminineVoice()
            setupUtteranceListener()
            isInitialized = true
        } else {
            Log.e("TextToSpeechManager", "TTS initialization failed with status: $status")
        }
    }

    private fun selectBestFeminineVoice() {
        try {
            val voices = tts?.voices ?: return
            // Look for high-quality female English voice
            val bestVoice = voices.firstOrNull { voice ->
                val name = voice.name.lowercase()
                !voice.isNetworkConnectionRequired &&
                        (name.contains("female") || name.contains("en-us-x-sfg") || name.contains("en_us_female") || name.contains("eva"))
            } ?: voices.firstOrNull { voice ->
                voice.locale.language == Locale.ENGLISH.language && voice.name.lowercase().contains("female")
            } ?: voices.firstOrNull { voice ->
                voice.locale.language == Locale.ENGLISH.language && !voice.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
            }

            if (bestVoice != null) {
                tts?.voice = bestVoice
            }
        } catch (e: Exception) {
            Log.w("TextToSpeechManager", "Could not set custom voice: ${e.message}")
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _speechAmplitude.value = 0.8f
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _speechAmplitude.value = 0f
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _speechAmplitude.value = 0f
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
                _speechAmplitude.value = 0f
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                // Modulate speech wave amplitude as words change
                _speechAmplitude.value = if (_speechAmplitude.value > 0.5f) 0.35f else 0.85f
            }
        })
    }

    fun speak(text: String, mood: Mood = Mood.HAPPY, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            onDone?.invoke()
            return
        }

        // Apply voice mode + mood pitch & speed
        val mode = preferences.getVoiceMode()
        val basePitch = preferences.getVoicePitch()
        val baseSpeed = preferences.getSpeakingSpeed()
        val cuteLevel = preferences.getCuteLevel()

        // Ultra Cute voice tuning
        val modeMultiplier = when (mode) {
            VoiceMode.ULTRA_CUTE -> 1.25f + (cuteLevel * 0.12f)
            VoiceMode.PLAYFUL -> 1.15f
            VoiceMode.CARING -> 1.02f
            VoiceMode.CALM -> 0.98f
            VoiceMode.STANDARD -> 1.06f
        }

        val finalPitch = (basePitch * modeMultiplier * mood.pitchMultiplier).coerceIn(0.5f, 2.0f)
        val finalRate = (baseSpeed * mode.baseSpeed * mood.speechRateMultiplier).coerceIn(0.6f, 1.8f)

        tts?.setPitch(finalPitch)
        tts?.setSpeechRate(finalRate)

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, preferences.getVoiceVolume())
        }

        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun previewVoice(mode: VoiceMode) {
        val pitch = mode.basePitch * (0.95f + preferences.getCuteLevel() * 0.15f)
        val rate = mode.baseSpeed * preferences.getSpeakingSpeed()
        tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
        tts?.setSpeechRate(rate.coerceIn(0.6f, 1.8f))

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, preferences.getVoiceVolume())
        }
        tts?.speak(mode.previewPhrase, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _speechAmplitude.value = 0f
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
