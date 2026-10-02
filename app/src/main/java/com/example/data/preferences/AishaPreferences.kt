package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.model.VoiceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AishaPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aisha_settings_prefs", Context.MODE_PRIVATE)

    private val _voiceModeFlow = MutableStateFlow(getVoiceMode())
    val voiceModeFlow: StateFlow<VoiceMode> = _voiceModeFlow.asStateFlow()

    private val _cuteLevelFlow = MutableStateFlow(getCuteLevel())
    val cuteLevelFlow: StateFlow<Float> = _cuteLevelFlow.asStateFlow()

    private val _speedFlow = MutableStateFlow(getSpeakingSpeed())
    val speedFlow: StateFlow<Float> = _speedFlow.asStateFlow()

    private val _pitchFlow = MutableStateFlow(getVoicePitch())
    val pitchFlow: StateFlow<Float> = _pitchFlow.asStateFlow()

    private val _bgVoiceFlow = MutableStateFlow(isBackgroundVoiceEnabled())
    val bgVoiceFlow: StateFlow<Boolean> = _bgVoiceFlow.asStateFlow()

    private val _memoryEnabledFlow = MutableStateFlow(isMemoryEnabled())
    val memoryEnabledFlow: StateFlow<Boolean> = _memoryEnabledFlow.asStateFlow()

    fun getVoiceMode(): VoiceMode {
        val id = prefs.getString(KEY_VOICE_MODE, VoiceMode.ULTRA_CUTE.id)
        return VoiceMode.fromId(id)
    }

    fun setVoiceMode(mode: VoiceMode) {
        prefs.edit().putString(KEY_VOICE_MODE, mode.id).apply()
        _voiceModeFlow.value = mode
    }

    fun getSpeakingSpeed(): Float = prefs.getFloat(KEY_SPEAKING_SPEED, 1.0f)
    fun setSpeakingSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_SPEAKING_SPEED, speed).apply()
        _speedFlow.value = speed
    }

    fun getVoicePitch(): Float = prefs.getFloat(KEY_VOICE_PITCH, 1.25f)
    fun setVoicePitch(pitch: Float) {
        prefs.edit().putFloat(KEY_VOICE_PITCH, pitch).apply()
        _pitchFlow.value = pitch
    }

    fun getVoiceVolume(): Float = prefs.getFloat(KEY_VOICE_VOLUME, 1.0f)
    fun setVoiceVolume(vol: Float) = prefs.edit().putFloat(KEY_VOICE_VOLUME, vol).apply()

    fun getCuteLevel(): Float = prefs.getFloat(KEY_CUTE_LEVEL, 0.95f)
    fun setCuteLevel(level: Float) {
        prefs.edit().putFloat(KEY_CUTE_LEVEL, level).apply()
        _cuteLevelFlow.value = level
    }

    fun getEmotionalExpression(): Float = prefs.getFloat(KEY_EMOTIONAL_EXPRESSION, 0.90f)
    fun setEmotionalExpression(level: Float) = prefs.edit().putFloat(KEY_EMOTIONAL_EXPRESSION, level).apply()

    fun getCaringLevel(): Float = prefs.getFloat(KEY_CARING_LEVEL, 0.95f)
    fun setCaringLevel(level: Float) = prefs.edit().putFloat(KEY_CARING_LEVEL, level).apply()

    fun getPlayfulnessLevel(): Float = prefs.getFloat(KEY_PLAYFULNESS_LEVEL, 0.85f)
    fun setPlayfulnessLevel(level: Float) = prefs.edit().putFloat(KEY_PLAYFULNESS_LEVEL, level).apply()

    fun getAffectionLevel(): Float = prefs.getFloat(KEY_AFFECTION_LEVEL, 0.90f)
    fun setAffectionLevel(level: Float) = prefs.edit().putFloat(KEY_AFFECTION_LEVEL, level).apply()

    fun getJealousyLevel(): Float = prefs.getFloat(KEY_JEALOUSY_LEVEL, 0.65f)
    fun setJealousyLevel(level: Float) = prefs.edit().putFloat(KEY_JEALOUSY_LEVEL, level).apply()

    fun isMemoryEnabled(): Boolean = prefs.getBoolean(KEY_MEMORY_ENABLED, true)
    fun setMemoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MEMORY_ENABLED, enabled).apply()
        _memoryEnabledFlow.value = enabled
    }

    fun isBackgroundVoiceEnabled(): Boolean = prefs.getBoolean(KEY_BG_VOICE_ENABLED, false)
    fun setBackgroundVoiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BG_VOICE_ENABLED, enabled).apply()
        _bgVoiceFlow.value = enabled
    }

    fun isContinuousListening(): Boolean = prefs.getBoolean(KEY_CONTINUOUS_LISTENING, true)
    fun setContinuousListening(continuous: Boolean) =
        prefs.edit().putBoolean(KEY_CONTINUOUS_LISTENING, continuous).apply()

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Sweetheart") ?: "Sweetheart"
    fun setUserName(name: String) = prefs.edit().putString(KEY_USER_NAME, name).apply()

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    fun setOnboardingCompleted(completed: Boolean) =
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()

    companion object {
        private const val KEY_VOICE_MODE = "voice_mode"
        private const val KEY_SPEAKING_SPEED = "speaking_speed"
        private const val KEY_VOICE_PITCH = "voice_pitch"
        private const val KEY_VOICE_VOLUME = "voice_volume"
        private const val KEY_CUTE_LEVEL = "cute_level"
        private const val KEY_EMOTIONAL_EXPRESSION = "emotional_expression"
        private const val KEY_CARING_LEVEL = "caring_level"
        private const val KEY_PLAYFULNESS_LEVEL = "playfulness_level"
        private const val KEY_AFFECTION_LEVEL = "affection_level"
        private const val KEY_JEALOUSY_LEVEL = "jealousy_level"
        private const val KEY_MEMORY_ENABLED = "memory_enabled"
        private const val KEY_BG_VOICE_ENABLED = "bg_voice_enabled"
        private const val KEY_CONTINUOUS_LISTENING = "continuous_listening"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
