package com.example.wizardquiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4B2E83),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF6C4A2F),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFB08900),
    background = Color(0xFFF8F7FB),
    surface = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5D9FF),
    secondaryContainer = Color(0xFFF2E2D4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC9B3FF),
    onPrimary = Color(0xFF2B145C),
    secondary = Color(0xFFE3C4A9),
    onSecondary = Color(0xFF3B2513),
    tertiary = Color(0xFFFFD76B),
    background = Color(0xFF121015),
    surface = Color(0xFF1A1720),
    primaryContainer = Color(0xFF3D296B),
    secondaryContainer = Color(0xFF4A3323)
)

@Composable
fun WizardQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
