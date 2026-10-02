package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.VoiceMode
import com.example.ui.AishaViewModel
import com.example.ui.theme.AishaCardBorder
import com.example.ui.theme.AishaDeepBackground
import com.example.ui.theme.AishaGoldenAmber
import com.example.ui.theme.AishaLavender
import com.example.ui.theme.AishaNeonCyan
import com.example.ui.theme.AishaPrimary
import com.example.ui.theme.AishaRosePink
import com.example.ui.theme.AishaSoftPink
import com.example.ui.theme.AishaSurface
import com.example.ui.theme.AishaSurfaceGlass
import com.example.ui.theme.AishaSurfaceVariant
import com.example.ui.theme.AishaTextMuted
import com.example.ui.theme.AishaTextPrimary
import com.example.ui.theme.AishaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(
    viewModel: AishaViewModel,
    onNavigateBack: () -> Unit
) {
    val activeVoiceMode by viewModel.voiceMode.collectAsStateWithLifecycle()
    val speakingSpeed by viewModel.speakingSpeed.collectAsStateWithLifecycle()
    val voicePitch by viewModel.voicePitch.collectAsStateWithLifecycle()
    val cuteLevel by viewModel.cuteLevel.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AishaDeepBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Voice & Speech Settings",
                        color = AishaTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("voice_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AishaTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AishaDeepBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurfaceGlass),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AishaRosePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = AishaRosePink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ultra Cute Voice Engine",
                            color = AishaRosePink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Soft, feminine, affectionate voice modulation with real-time mood pitch shifting.",
                            color = AishaTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Voice Modes Selection
            Text(
                text = "SELECT VOICE MODE",
                color = AishaTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            VoiceMode.entries.forEach { mode ->
                val isSelected = mode == activeVoiceMode

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AishaSurfaceVariant else AishaSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) AishaRosePink else AishaCardBorder.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setVoiceMode(mode) }
                        .testTag("voice_mode_${mode.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mode.displayName,
                                    color = if (isSelected) AishaRosePink else AishaTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (mode == VoiceMode.ULTRA_CUTE) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AishaRosePink.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "POPULAR ❤️",
                                            color = AishaRosePink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.subtitle,
                                color = AishaTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Preview button
                            IconButton(
                                onClick = { viewModel.previewVoice(mode) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("preview_voice_${mode.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Preview voice",
                                    tint = AishaNeonCyan
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = AishaRosePink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Sliders Section
            Text(
                text = "FINE-TUNE VOICE EXPRESSION",
                color = AishaTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cute Level Slider
                    VoiceSliderItem(
                        title = "Cute & Sweet Level",
                        subtitle = "Bubbly sweetness & warmth",
                        value = cuteLevel,
                        valueDisplay = "${(cuteLevel * 100).toInt()}%",
                        color = AishaRosePink,
                        onValueChange = { viewModel.setCuteLevel(it) }
                    )

                    // Speaking Speed Slider
                    VoiceSliderItem(
                        title = "Speaking Speed",
                        subtitle = "Natural conversational tempo",
                        value = (speakingSpeed - 0.7f) / (1.4f - 0.7f),
                        valueDisplay = "${String.format("%.2f", speakingSpeed)}x",
                        color = AishaNeonCyan,
                        onValueChange = {
                            val actual = 0.7f + it * (1.4f - 0.7f)
                            viewModel.setSpeakingSpeed(actual)
                        }
                    )

                    // Voice Pitch Slider
                    VoiceSliderItem(
                        title = "Voice Pitch",
                        subtitle = "Soft feminine tonality",
                        value = (voicePitch - 0.8f) / (1.6f - 0.8f),
                        valueDisplay = "${String.format("%.2f", voicePitch)}x",
                        color = AishaLavender,
                        onValueChange = {
                            val actual = 0.8f + it * (1.6f - 0.8f)
                            viewModel.setVoicePitch(actual)
                        }
                    )
                }
            }

            // Test Voice Button
            Button(
                onClick = { viewModel.previewVoice(activeVoiceMode) },
                colors = ButtonDefaults.buttonColors(containerColor = AishaPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("test_voice_button")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test ${activeVoiceMode.displayName} Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun VoiceSliderItem(
    title: String,
    subtitle: String,
    value: Float,
    valueDisplay: String,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = AishaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = AishaTextSecondary,
                    fontSize = 11.sp
                )
            }
            Text(
                text = valueDisplay,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value.coerceIn(0f, 1f),
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = AishaSurfaceVariant
            )
        )
    }
}
