package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AishaDarkColorScheme = darkColorScheme(
    primary = AishaPrimary,
    onPrimary = AishaOnPrimary,
    primaryContainer = AishaPrimaryContainer,
    onPrimaryContainer = AishaOnPrimaryContainer,
    secondary = AishaSecondary,
    onSecondary = AishaOnSecondary,
    secondaryContainer = AishaSecondaryContainer,
    onSecondaryContainer = AishaOnSecondaryContainer,
    tertiary = AishaTertiary,
    onTertiary = AishaOnTertiary,
    tertiaryContainer = AishaTertiaryContainer,
    onTertiaryContainer = AishaOnTertiaryContainer,
    background = AishaDeepBackground,
    onBackground = AishaTextPrimary,
    surface = AishaSurface,
    onSurface = AishaTextPrimary,
    surfaceVariant = AishaSurfaceVariant,
    onSurfaceVariant = AishaTextSecondary,
    outline = AishaCardBorder
)

@Composable
fun AishaTheme(
    darkTheme: Boolean = true, // Aisha is styled as a premium dark futuristic companion
    content: @Composable () -> Unit
) {
    val colorScheme = AishaDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = AishaDeepBackground.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = AishaDeepBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AishaTheme(darkTheme = true, content = content)
}
