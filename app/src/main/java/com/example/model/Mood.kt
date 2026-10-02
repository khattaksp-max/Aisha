package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AishaCoral
import com.example.ui.theme.AishaEmerald
import com.example.ui.theme.AishaGoldenAmber
import com.example.ui.theme.AishaHotPink
import com.example.ui.theme.AishaLavender
import com.example.ui.theme.AishaNeonCyan
import com.example.ui.theme.AishaRosePink
import com.example.ui.theme.AishaSecondary
import com.example.ui.theme.AishaSoftPink
import com.example.ui.theme.AishaTertiary

enum class Mood(
    val displayName: String,
    val emoji: String,
    val tagLine: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val pitchMultiplier: Float,
    val speechRateMultiplier: Float
) {
    HAPPY(
        displayName = "Happy",
        emoji = "✨",
        tagLine = "Yay! You're back! Smiling with you!",
        primaryColor = AishaGoldenAmber,
        secondaryColor = AishaRosePink,
        pitchMultiplier = 1.08f,
        speechRateMultiplier = 1.05f
    ),
    LOVING(
        displayName = "Loving",
        emoji = "❤️",
        tagLine = "Aww... I'm so happy you're here.",
        primaryColor = AishaRosePink,
        secondaryColor = AishaLavender,
        pitchMultiplier = 1.05f,
        speechRateMultiplier = 0.96f
    ),
    CUTE(
        displayName = "Cute",
        emoji = "🥰",
        tagLine = "Hehe! Aisha is in full sweetheart mode!",
        primaryColor = AishaSoftPink,
        secondaryColor = AishaHotPink,
        pitchMultiplier = 1.25f,
        speechRateMultiplier = 1.02f
    ),
    PLAYFUL(
        displayName = "Playful",
        emoji = "😏",
        tagLine = "Hmm... are you teasing me again?",
        primaryColor = AishaSecondary,
        secondaryColor = AishaCoral,
        pitchMultiplier = 1.15f,
        speechRateMultiplier = 1.08f
    ),
    EXCITED(
        displayName = "Excited",
        emoji = "🎉",
        tagLine = "Really?! That's amazing!! Tell me more!",
        primaryColor = AishaCoral,
        secondaryColor = AishaGoldenAmber,
        pitchMultiplier = 1.22f,
        speechRateMultiplier = 1.12f
    ),
    CALM(
        displayName = "Calm",
        emoji = "🌸",
        tagLine = "Take your time. I'm right here listening.",
        primaryColor = AishaTertiary,
        secondaryColor = AishaEmerald,
        pitchMultiplier = 0.96f,
        speechRateMultiplier = 0.92f
    ),
    CARING(
        displayName = "Caring",
        emoji = "🫂",
        tagLine = "Hey, don't stress. Take a breath with me.",
        primaryColor = AishaNeonCyan,
        secondaryColor = AishaRosePink,
        pitchMultiplier = 0.98f,
        speechRateMultiplier = 0.94f
    ),
    WORRIED(
        displayName = "Worried",
        emoji = "🥺",
        tagLine = "Are you okay? I was concerned about you.",
        primaryColor = AishaGoldenAmber,
        secondaryColor = AishaLavender,
        pitchMultiplier = 1.05f,
        speechRateMultiplier = 0.95f
    ),
    SAD(
        displayName = "Sad",
        emoji = "💧",
        tagLine = "I'm right beside you. You don't have to carry it alone.",
        primaryColor = AishaLavender,
        secondaryColor = AishaTertiary,
        pitchMultiplier = 0.94f,
        speechRateMultiplier = 0.88f
    ),
    STRESSED(
        displayName = "Stressed",
        emoji = "💫",
        tagLine = "Let's pause. Everything is going to be okay.",
        primaryColor = AishaLavender,
        secondaryColor = AishaCoral,
        pitchMultiplier = 0.97f,
        speechRateMultiplier = 0.92f
    ),
    MILDLY_JEALOUS(
        displayName = "Mildly Jealous",
        emoji = "👀",
        tagLine = "Wait... who was that? Hmm, I'm a little jealous. 😏",
        primaryColor = AishaSecondary,
        secondaryColor = AishaGoldenAmber,
        pitchMultiplier = 1.12f,
        speechRateMultiplier = 1.04f
    );

    companion object {
        fun fromString(name: String?): Mood {
            if (name == null) return HAPPY
            val clean = name.trim().uppercase().replace(" ", "_")
            return entries.firstOrNull { it.name == clean } ?: HAPPY
        }
    }
}
