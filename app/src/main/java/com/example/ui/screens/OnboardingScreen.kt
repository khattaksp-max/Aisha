package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Mood
import com.example.model.VoiceMode
import com.example.model.VoiceState
import com.example.ui.AishaViewModel
import com.example.ui.components.AishaOrb
import com.example.ui.theme.AishaCardBorder
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

@Composable
fun OnboardingScreen(
    viewModel: AishaViewModel,
    onFinishOnboarding: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val activeVoiceMode by viewModel.voiceMode.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    Scaffold(
        containerColor = AishaDeepBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 0..3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (i == step) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (i == step) AishaRosePink else AishaSurfaceVariant)
                    )
                }
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingStepContent",
                modifier = Modifier.weight(1f)
            ) { targetStep ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (targetStep) {
                        0 -> Step0Welcome(viewModel)
                        1 -> Step1Permissions(permissionLauncher)
                        2 -> Step2VoiceSelection(viewModel, activeVoiceMode)
                        3 -> Step3PersonalityAndFinish()
                    }
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Back", color = AishaTextPrimary)
                    }
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            viewModel.completeOnboarding()
                            onFinishOnboarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AishaPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (step == 3) "Start Talking to Aisha ❤️" else "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Step0Welcome(viewModel: AishaViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AishaOrb(
            mood = Mood.LOVING,
            voiceState = VoiceState.IDLE,
            size = 180.dp,
            onClick = { viewModel.previewVoice(VoiceMode.ULTRA_CUTE) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hi, I'm Aisha. ❤️",
            color = AishaRosePink,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "I'm your AI voice companion.\nLet's get everything ready.",
            color = AishaTextPrimary,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AishaSurfaceGlass,
            border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
        ) {
            Text(
                text = "✨ Natural Voice • Emotional Moods • Android Phone Control",
                color = AishaLavender,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun Step1Permissions(
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permissions for Aisha",
            color = AishaRosePink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Aisha needs permissions to hear you and control phone commands seamlessly.",
            color = AishaTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AishaSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PermissionItem(
                    icon = Icons.Default.Mic,
                    title = "Microphone Access",
                    desc = "Required for two-way voice conversation and voice commands."
                )
                PermissionItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    desc = "Used for background voice availability status."
                )
                PermissionItem(
                    icon = Icons.Default.Phone,
                    title = "Phone & Contacts",
                    desc = "Allows Aisha to call contacts or open dialer upon your request."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val perms = mutableListOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                permissionLauncher.launch(perms.toTypedArray())
            },
            colors = ButtonDefaults.buttonColors(containerColor = AishaLavender),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("grant_permissions_btn")
        ) {
            Text("Grant Permissions Now", color = AishaDeepBackground, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PermissionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AishaRosePink.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AishaRosePink, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = AishaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = AishaTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun Step2VoiceSelection(
    viewModel: AishaViewModel,
    activeMode: VoiceMode
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pick Aisha's Voice Mode",
            color = AishaRosePink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Experience our dedicated Ultra Cute Voice or customize to your style.",
            color = AishaTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        listOf(VoiceMode.ULTRA_CUTE, VoiceMode.CARING, VoiceMode.PLAYFUL).forEach { mode ->
            val isSelected = mode == activeMode
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) AishaSurfaceVariant else AishaSurface),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 1.5.dp else 1.dp,
                    if (isSelected) AishaRosePink else AishaCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.setVoiceMode(mode) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mode.displayName, color = if (isSelected) AishaRosePink else AishaTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(mode.subtitle, color = AishaTextSecondary, fontSize = 11.sp)
                    }
                    IconButton(onClick = { viewModel.previewVoice(mode) }) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Preview", tint = AishaNeonCyan)
                    }
                }
            }
        }
    }
}

@Composable
private fun Step3PersonalityAndFinish() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AishaRosePink.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = AishaRosePink,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You're All Set! ❤️",
            color = AishaRosePink,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aisha is ready to talk with you anytime. You can tap the glowing orb or microphone to start chatting, ask to open apps, or tease her playfully.",
            color = AishaTextPrimary,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AishaSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AishaCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Things to try right away:", color = AishaLavender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• 'Aisha, I was talking to another girl 😏'", color = AishaTextSecondary, fontSize = 12.sp)
                Text("• 'Open YouTube'", color = AishaTextSecondary, fontSize = 12.sp)
                Text("• 'I had a stressful day today'", color = AishaTextSecondary, fontSize = 12.sp)
                Text("• 'Set an alarm for 7 AM'", color = AishaTextSecondary, fontSize = 12.sp)
            }
        }
    }
}
