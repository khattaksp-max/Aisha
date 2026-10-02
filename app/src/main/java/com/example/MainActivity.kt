package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.AishaViewModel
import com.example.ui.screens.MainAssistantScreen
import com.example.ui.screens.MemoryManagementScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.theme.AishaDeepBackground
import com.example.ui.theme.AishaTheme

enum class Screen {
    ONBOARDING,
    MAIN,
    SETTINGS,
    VOICE_SETTINGS,
    MEMORY
}

class MainActivity : ComponentActivity() {

    private val viewModel: AishaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AishaApplication
        val initialScreen = try {
            if (app.preferences.isOnboardingCompleted()) Screen.MAIN else Screen.ONBOARDING
        } catch (_: Throwable) {
            Screen.MAIN
        }

        setContent {
            AishaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AishaDeepBackground
                ) {
                    var currentScreen by remember { mutableStateOf(initialScreen) }

                    when (currentScreen) {
                        Screen.ONBOARDING -> {
                            OnboardingScreen(
                                viewModel = viewModel,
                                onFinishOnboarding = {
                                    currentScreen = Screen.MAIN
                                }
                            )
                        }

                        Screen.MAIN -> {
                            MainAssistantScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onNavigateToVoiceSettings = { currentScreen = Screen.VOICE_SETTINGS }
                            )
                        }

                        Screen.SETTINGS -> {
                            BackHandler { currentScreen = Screen.MAIN }
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = Screen.MAIN },
                                onNavigateToVoiceSettings = { currentScreen = Screen.VOICE_SETTINGS },
                                onNavigateToMemory = { currentScreen = Screen.MEMORY }
                            )
                        }

                        Screen.VOICE_SETTINGS -> {
                            BackHandler { currentScreen = Screen.MAIN }
                            VoiceSettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = Screen.MAIN }
                            )
                        }

                        Screen.MEMORY -> {
                            BackHandler { currentScreen = Screen.SETTINGS }
                            MemoryManagementScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = Screen.SETTINGS }
                            )
                        }
                    }
                }
            }
        }
    }
}
