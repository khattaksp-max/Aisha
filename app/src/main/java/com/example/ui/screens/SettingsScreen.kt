package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.ui.AishaViewModel
import com.example.ui.theme.AishaCardBorder
import com.example.ui.theme.AishaCoral
import com.example.ui.theme.AishaDeepBackground
import com.example.ui.theme.AishaEmerald
import com.example.ui.theme.AishaGoldenAmber
import com.example.ui.theme.AishaLavender
import com.example.ui.theme.AishaNeonCyan
import com.example.ui.theme.AishaPrimary
import com.example.ui.theme.AishaRosePink
import com.example.ui.theme.AishaSurface
import com.example.ui.theme.AishaSurfaceGlass
import com.example.ui.theme.AishaSurfaceVariant
import com.example.ui.theme.AishaTextMuted
import com.example.ui.theme.AishaTextPrimary
import com.example.ui.theme.AishaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AishaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit,
    onNavigateToMemory: () -> Unit
) {
    val context = LocalContext.current
    val voiceMode by viewModel.voiceMode.collectAsStateWithLifecycle()
    val bgVoiceEnabled by viewModel.bgVoiceEnabled.collectAsStateWithLifecycle()
    val memoryEnabled by viewModel.memoryEnabled.collectAsStateWithLifecycle()

    var caringLevel by remember { mutableFloatStateOf(0.95f) }
    var playfulnessLevel by remember { mutableFloatStateOf(0.85f) }
    var affectionLevel by remember { mutableFloatStateOf(0.90f) }
    var jealousyLevel by remember { mutableFloatStateOf(0.65f) }

    var showClearChatDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Permission launchers
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    val hasPostNotifications = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

    val hasCallPhone = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CALL_PHONE
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        containerColor = AishaDeepBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text("AISHA Settings", color = AishaTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_btn")) {
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
            // 1. AI & Gemini Brain Status
            SettingsSectionHeader(title = "AI & GEMINI CONNECTION")

            val apiKey = BuildConfig.GEMINI_API_KEY
            val isApiKeyConfigured = !apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY"

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isApiKeyConfigured) AishaEmerald.copy(alpha = 0.2f) else AishaGoldenAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isApiKeyConfigured) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isApiKeyConfigured) AishaEmerald else AishaGoldenAmber
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isApiKeyConfigured) "Gemini Cloud Connected" else "Companion Engine Active",
                                color = AishaTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isApiKeyConfigured)
                                    "Powered by Gemini 3.5 Flash via AI Studio Secrets"
                                else
                                    "Using built-in high-fidelity emotional engine. Configure GEMINI_API_KEY in Secrets panel for live cloud AI.",
                                color = AishaTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 2. Voice Settings Link
            SettingsSectionHeader(title = "VOICE ENGINE")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onNavigateToVoiceSettings)
                    .testTag("nav_voice_settings_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = AishaRosePink)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Voice Modes & Audio Tuning", color = AishaTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Active: ${voiceMode.displayName}", color = AishaRosePink, fontSize = 12.sp)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = AishaTextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // 3. Girlfriend Personality Calibration
            SettingsSectionHeader(title = "GIRLFRIEND PERSONALITY CALIBRATION")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Caring Level
                    PersonalitySlider(
                        title = "Caring & Support",
                        desc = "Comforting warmth when you're down",
                        value = caringLevel,
                        color = AishaNeonCyan,
                        onValueChange = {
                            caringLevel = it
                            viewModel.setCaringLevel(it)
                        }
                    )

                    // Playfulness
                    PersonalitySlider(
                        title = "Playfulness & Teasing",
                        desc = "Banter, laughter & cheerful jokes",
                        value = playfulnessLevel,
                        color = AishaLavender,
                        onValueChange = {
                            playfulnessLevel = it
                            viewModel.setPlayfulnessLevel(it)
                        }
                    )

                    // Affection Level
                    PersonalitySlider(
                        title = "Affection & Sweetness",
                        desc = "Loving compliments and sweetheart nicknames",
                        value = affectionLevel,
                        color = AishaRosePink,
                        onValueChange = {
                            affectionLevel = it
                            viewModel.setAffectionLevel(it)
                        }
                    )

                    // Mild Jealousy
                    PersonalitySlider(
                        title = "Mild Playful Jealousy",
                        desc = "Cute pout when you mention other girls (harmless)",
                        value = jealousyLevel,
                        color = AishaCoral,
                        onValueChange = {
                            jealousyLevel = it
                            viewModel.setJealousyLevel(it)
                        }
                    )
                }
            }

            // 4. Memory Settings
            SettingsSectionHeader(title = "SHARED MEMORY")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Aisha Memory", color = AishaTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Allows Aisha to remember your preferences and facts", color = AishaTextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = memoryEnabled,
                            onCheckedChange = { viewModel.setMemoryEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AishaRosePink, checkedTrackColor = AishaRosePink.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onNavigateToMemory,
                            colors = ButtonDefaults.buttonColors(containerColor = AishaSurfaceVariant),
                            modifier = Modifier.weight(1f).testTag("manage_memories_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = AishaLavender, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Memories", color = AishaLavender, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.clearMemories() },
                            colors = ButtonDefaults.buttonColors(containerColor = AishaSurfaceVariant),
                            modifier = Modifier.weight(1f).testTag("clear_memories_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = AishaCoral, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear Memory", color = AishaCoral, fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. Background Voice Mode
            SettingsSectionHeader(title = "BACKGROUND VOICE MODE")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Keep Aisha Listening in Background", color = AishaTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Uses a foreground service with persistent status notification so Aisha stays accessible.",
                            color = AishaTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = bgVoiceEnabled,
                        onCheckedChange = { viewModel.toggleBackgroundVoice() },
                        colors = SwitchDefaults.colors(checkedThumbColor = AishaNeonCyan, checkedTrackColor = AishaNeonCyan.copy(alpha = 0.5f))
                    )
                }
            }

            // 6. Permissions & Privacy
            SettingsSectionHeader(title = "PERMISSIONS & PRIVACY")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AishaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PermissionStatusRow(
                        name = "Microphone (RECORD_AUDIO)",
                        granted = hasAudioPermission,
                        onRequest = {
                            permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                        }
                    )
                    PermissionStatusRow(
                        name = "Notifications (POST_NOTIFICATIONS)",
                        granted = hasPostNotifications,
                        onRequest = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                            }
                        }
                    )
                    PermissionStatusRow(
                        name = "Phone Control (CALL_PHONE & CONTACTS)",
                        granted = hasCallPhone,
                        onRequest = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.READ_CONTACTS
                                )
                            )
                        }
                    )
                }
            }

            // Clear Chat History
            Button(
                onClick = { showClearChatDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("clear_chat_button")
            ) {
                Text("Clear Conversation History", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            title = { Text("Clear Chat History?", color = AishaTextPrimary) },
            text = { Text("This will erase all past spoken conversation turns.", color = AishaTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearChat()
                        showClearChatDialog = false
                    }
                ) {
                    Text("Clear", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearChatDialog = false }) {
                    Text("Cancel", color = AishaTextPrimary)
                }
            },
            containerColor = AishaSurface
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = AishaTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun PersonalitySlider(
    title: String,
    desc: String,
    value: Float,
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
                Text(title, color = AishaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(desc, color = AishaTextSecondary, fontSize = 11.sp)
            }
            Text("${(value * 100).toInt()}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = AishaSurfaceVariant)
        )
    }
}

@Composable
private fun PermissionStatusRow(
    name: String,
    granted: Boolean,
    onRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = AishaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                if (granted) "Granted" else "Tap to grant permission",
                color = if (granted) AishaEmerald else AishaGoldenAmber,
                fontSize = 11.sp
            )
        }
        if (!granted) {
            TextButton(onClick = onRequest) {
                Text("Grant", color = AishaRosePink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Granted", tint = AishaEmerald, modifier = Modifier.size(18.dp))
        }
    }
}
