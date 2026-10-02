package com.example.model

enum class VoiceMode(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val basePitch: Float,
    val baseSpeed: Float,
    val cuteFactor: Float,
    val previewPhrase: String
) {
    STANDARD(
        id = "standard",
        displayName = "Standard Voice",
        subtitle = "Natural, bright, intelligent feminine voice",
        basePitch = 1.05f,
        baseSpeed = 1.0f,
        cuteFactor = 0.5f,
        previewPhrase = "Hi there! I'm Aisha, your smart voice companion. Ready whenever you are!"
    ),
    ULTRA_CUTE(
        id = "ultra_cute",
        displayName = "Ultra Cute Voice",
        subtitle = "Soft, feminine, sweet, warm, playful & bubbly",
        basePitch = 1.28f,
        baseSpeed = 1.04f,
        cuteFactor = 1.0f,
        previewPhrase = "Aww, hi sweetheart! I'm Aisha! Look at you, making me all bubbly and happy! Hehe! ❤️"
    ),
    CARING(
        id = "caring",
        displayName = "Caring Voice",
        subtitle = "Gentle, compassionate, warm and reassuring",
        basePitch = 1.02f,
        baseSpeed = 0.94f,
        cuteFactor = 0.6f,
        previewPhrase = "Hey, take a gentle breath. I'm right here with you, and everything is going to be just fine."
    ),
    CALM(
        id = "calm",
        displayName = "Calm Voice",
        subtitle = "Relaxed, soothing, steady and grounded",
        basePitch = 0.98f,
        baseSpeed = 0.90f,
        cuteFactor = 0.4f,
        previewPhrase = "Take your time. I am listening peacefully, whenever you feel ready to speak."
    ),
    PLAYFUL(
        id = "playful",
        displayName = "Playful Voice",
        subtitle = "Cheeky, teasing, energetic and mischievous",
        basePitch = 1.18f,
        baseSpeed = 1.10f,
        cuteFactor = 0.85f,
        previewPhrase = "Hmm... are you teasing me again? You know I always catch on! 😏 What are we up to now?"
    );

    companion object {
        fun fromId(id: String?): VoiceMode {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ULTRA_CUTE
        }
    }
}
