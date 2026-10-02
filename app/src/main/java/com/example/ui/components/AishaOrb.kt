package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Mood
import com.example.model.VoiceState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AishaOrb(
    mood: Mood,
    voiceState: VoiceState,
    rmsDb: Float = 0f,
    speechAmplitude: Float = 0f,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbInfiniteTransition")

    // Breathing pulse
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingPulse"
    )

    // Orbital rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (voiceState == VoiceState.THINKING) 1200 else 6000,
                easing = LinearEasing
            )
        ),
        label = "OrbitalRotation"
    )

    // Wave ripple
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleProgress"
    )

    // Dynamic colors based on mood
    val animatedPrimaryColor by animateColorAsState(
        targetValue = mood.primaryColor,
        animationSpec = tween(600),
        label = "PrimaryColor"
    )
    val animatedSecondaryColor by animateColorAsState(
        targetValue = mood.secondaryColor,
        animationSpec = tween(600),
        label = "SecondaryColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "Aisha AI Orb in ${mood.displayName} state, currently $voiceState" }
            .testTag("aisha_ai_orb")
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 3.4f

            // Dynamic scaling based on state & sound
            val scale = when (voiceState) {
                VoiceState.LISTENING -> 1.0f + (rmsDb * 0.35f)
                VoiceState.SPEAKING -> 1.0f + (speechAmplitude * 0.28f)
                VoiceState.THINKING -> 1.02f
                VoiceState.IDLE -> breathingPulse
                VoiceState.ERROR -> 0.95f
            }
            val currentRadius = baseRadius * scale

            // 1. Outer Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedPrimaryColor.copy(alpha = 0.45f),
                        animatedSecondaryColor.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.8f
                ),
                radius = currentRadius * 1.8f,
                center = center
            )

            // 2. State-specific Ripple Rings
            if (voiceState == VoiceState.LISTENING) {
                val ringRadius = currentRadius + (rippleProgress * currentRadius * 0.9f)
                val alpha = (1f - rippleProgress) * 0.7f
                drawCircle(
                    color = animatedSecondaryColor.copy(alpha = alpha),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            } else if (voiceState == VoiceState.SPEAKING) {
                val waveCount = 3
                for (i in 0 until waveCount) {
                    val phase = (rippleProgress + (i.toFloat() / waveCount)) % 1f
                    val r = currentRadius * (1f + (phase * 0.65f))
                    val a = (1f - phase) * (0.6f * speechAmplitude.coerceAtLeast(0.3f))
                    drawCircle(
                        color = animatedPrimaryColor.copy(alpha = a),
                        radius = r,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }

            // 3. Main Orb Core Sphere (Rich Holographic Gradient)
            val orbBrush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.92f),
                    animatedPrimaryColor,
                    animatedSecondaryColor,
                    Color(0xFF0F172A)
                ),
                center = Offset(center.x - currentRadius * 0.25f, center.y - currentRadius * 0.25f),
                radius = currentRadius
            )
            drawCircle(
                brush = orbBrush,
                radius = currentRadius,
                center = center
            )

            // 4. Orbital particles / halo ring
            drawOrbitalHalo(
                center = center,
                radius = currentRadius * 1.25f,
                rotationAngle = rotationAngle,
                color = animatedSecondaryColor,
                thinking = voiceState == VoiceState.THINKING
            )

            // 5. Specular highlight for crystal 3D look
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f),
                        Color.Transparent
                    ),
                    center = Offset(center.x - currentRadius * 0.35f, center.y - currentRadius * 0.35f),
                    radius = currentRadius * 0.45f
                ),
                radius = currentRadius * 0.45f,
                center = Offset(center.x - currentRadius * 0.35f, center.y - currentRadius * 0.35f)
            )
        }
    }
}

private fun DrawScope.drawOrbitalHalo(
    center: Offset,
    radius: Float,
    rotationAngle: Float,
    color: Color,
    thinking: Boolean
) {
    val dotCount = if (thinking) 6 else 4
    val strokeWidth = 1.5.dp.toPx()

    // Faint orbit ring
    drawCircle(
        color = color.copy(alpha = 0.25f),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth)
    )

    // Orbiting particles
    for (i in 0 until dotCount) {
        val angleRad = Math.toRadians((rotationAngle + (i * 360f / dotCount)).toDouble())
        val x = center.x + (radius * cos(angleRad)).toFloat()
        val y = center.y + (radius * sin(angleRad)).toFloat()
        val dotRadius = if (i == 0) 4.5.dp.toPx() else 3.dp.toPx()

        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = dotRadius,
            center = Offset(x, y)
        )
    }
}
