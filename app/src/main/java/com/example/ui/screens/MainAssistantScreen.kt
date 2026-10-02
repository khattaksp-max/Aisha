package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.Mood
import com.example.model.VoiceState
import com.example.ui.AishaViewModel
import com.example.ui.components.AishaOrb
import com.example.ui.theme.AishaCardBorder
import com.example.ui.theme.AishaDeepBackground
import com.example.ui.theme.AishaHotPink
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

@Composable
fun MainAssistantScreen(
    viewModel: AishaViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit
) {
    val currentMood by viewModel.currentMood.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val liveRmsDb by viewModel.liveRmsDb.collectAsStateWithLifecycle()
    val speechAmplitude by viewModel.speechAmplitude.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val voiceMode by viewModel.voiceMode.collectAsStateWithLifecycle()
    val bgVoiceEnabled by viewModel.bgVoiceEnabled.collectAsStateWithLifecycle()
    val lastActionFeedback by viewModel.lastActionFeedback.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    LaunchedEffect(lastActionFeedback) {
        lastActionFeedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissActionFeedback()
        }
    }

    val quickPrompts = listOf(
        "I was talking to another girl 😏",
        "Open YouTube",
        "Aww, I missed you ❤️",
        "I had a stressful day 🥺",
        "Open Camera 📸",
        "Set an alarm for 7 AM ⏰",
        "Open WhatsApp 💬",
        "Play music 🎵",
        "Turn on Flashlight 💡"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AishaDeepBackground,
        topBar = {
            TopAppBarSection(
                currentMood = currentMood,
                bgVoiceEnabled = bgVoiceEnabled,
                voiceModeName = voiceMode.displayName,
                onToggleBgVoice = { viewModel.toggleBackgroundVoice() },
                onNavigateToVoiceSettings = onNavigateToVoiceSettings,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AISHA Animated Orb & State Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AishaOrb(
                    mood = currentMood,
                    voiceState = voiceState,
                    rmsDb = liveRmsDb,
                    speechAmplitude = speechAmplitude,
                    size = 170.dp,
                    onClick = {
                        if (isListening || isSpeaking) {
                            viewModel.interruptAndStop()
                        } else {
                            viewModel.startListening()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Voice status label
                val statusText = when (voiceState) {
                    VoiceState.LISTENING -> "Listening to you... ❤️"
                    VoiceState.THINKING -> "Aisha is thinking..."
                    VoiceState.SPEAKING -> "Aisha is speaking... ✨"
                    VoiceState.ERROR -> "Voice error occurred"
                    VoiceState.IDLE -> "Tap orb or mic to talk"
                }

                Text(
                    text = statusText,
                    color = if (isListening) AishaRosePink else AishaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = currentMood.tagLine,
                    color = AishaTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )
            }

            // Quick Actions Horizontal Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(quickPrompts) { prompt ->
                    QuickActionChip(text = prompt) {
                        viewModel.sendTextMessage(prompt)
                    }
                }
            }

            // Conversation Transcript List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AishaSurfaceGlass)
                    .border(1.dp, AishaCardBorder, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                if (chatMessages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Say hi to Aisha! ❤️",
                                color = AishaRosePink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try: 'Aisha, I was talking to another girl' or 'Open YouTube'",
                                color = AishaTextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { msg ->
                            ChatMessageBubble(
                                message = msg,
                                onReplay = { viewModel.replayMessage(msg) }
                            )
                        }
                    }
                }
            }

            // Bottom Voice & Text Input Control Bar
            BottomInputBar(
                textInput = textInput,
                onTextChange = { textInput = it },
                onSend = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendTextMessage(textInput)
                        textInput = ""
                    }
                },
                isListening = isListening,
                isSpeaking = isSpeaking,
                onToggleMic = {
                    if (isListening) {
                        viewModel.stopListening()
                    } else {
                        viewModel.startListening()
                    }
                },
                onStop = { viewModel.interruptAndStop() }
            )
        }
    }
}

@Composable
private fun TopAppBarSection(
    currentMood: Mood,
    bgVoiceEnabled: Boolean,
    voiceModeName: String,
    onToggleBgVoice: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Mood Badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "AISHA",
                color = AishaRosePink,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = currentMood.primaryColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, currentMood.primaryColor.copy(alpha = 0.5f)),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentMood.emoji, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentMood.displayName,
                        color = currentMood.primaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Actions: Voice Mode, BG Voice toggle, Settings
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Voice mode shortcut
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AishaSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onNavigateToVoiceSettings)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voice Mode Settings",
                        tint = AishaLavender,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = voiceModeName,
                        color = AishaLavender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Background Voice Toggle Button
            IconButton(
                onClick = onToggleBgVoice,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("bg_voice_toggle_btn")
            ) {
                Icon(
                    imageVector = if (bgVoiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.MicOff,
                    contentDescription = "Background Voice Toggle",
                    tint = if (bgVoiceEnabled) AishaNeonCyan else AishaTextMuted
                )
            }

            // Settings Button
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = AishaTextPrimary
                )
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AishaSurfaceVariant.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder.copy(alpha = 0.4f)),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = AishaTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    onReplay: () -> Unit
) {
    val isAisha = message.sender == MessageSender.AISHA

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAisha) Alignment.Start else Alignment.End
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAisha) 4.dp else 16.dp,
                bottomEnd = if (isAisha) 16.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isAisha) AishaSurface else AishaPrimary.copy(alpha = 0.85f)
            ),
            border = if (isAisha) androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder) else null,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAisha) "AISHA ${message.mood.emoji}" else "You",
                        color = if (isAisha) message.mood.primaryColor else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isAisha) {
                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Replay voice",
                                tint = message.mood.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    color = if (isAisha) AishaTextPrimary else Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Optional Action Feedback Tag
                if (!message.actionTaken.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AishaNeonCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AishaNeonCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "⚡ Action: ${message.actionTaken}",
                            color = AishaNeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomInputBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isListening: Boolean,
    isSpeaking: Boolean,
    onToggleMic: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Text field for quiet queries
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextChange,
            placeholder = { Text("Message Aisha...", color = AishaTextMuted, fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AishaSurface,
                unfocusedContainerColor = AishaSurface,
                focusedBorderColor = AishaRosePink,
                unfocusedBorderColor = AishaCardBorder,
                focusedTextColor = AishaTextPrimary,
                unfocusedTextColor = AishaTextPrimary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            trailingIcon = {
                if (textInput.isNotBlank()) {
                    IconButton(onClick = onSend, modifier = Modifier.testTag("send_text_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = AishaRosePink
                        )
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .testTag("chat_text_input")
        )

        // Stop / Barge-in button (visible when speaking or listening)
        AnimatedVisibility(
            visible = isListening || isSpeaking,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(
                onClick = onStop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.2f))
                    .border(1.dp, Color.Red, CircleShape)
                    .testTag("barge_in_stop_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop voice immediately",
                    tint = Color.Red
                )
            }
        }

        // Main Mic Interaction Button
        val micColor = if (isListening) AishaHotPink else AishaPrimary
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            micColor,
                            micColor.copy(alpha = 0.75f)
                        )
                    )
                )
                .clickable(onClick = onToggleMic)
                .testTag("mic_button")
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop listening" else "Start talking to Aisha",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
