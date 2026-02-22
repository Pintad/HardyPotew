package com.example.wizardquiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFF97316),
    background = Color(0xFFF4F7FF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EEFF),
    onSurfaceVariant = Color(0xFF3A435C),
    primaryContainer = Color(0xFFE1E5FF),
    secondaryContainer = Color(0xFFDDF3FF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC1C4FF),
    onPrimary = Color(0xFF23218A),
    secondary = Color(0xFF8CDDFF),
    onSecondary = Color(0xFF003547),
    tertiary = Color(0xFFFFB482),
    background = Color(0xFF0D1220),
    surface = Color(0xFF161C2C),
    surfaceVariant = Color(0xFF252D43),
    onSurfaceVariant = Color(0xFFC2CADF),
    primaryContainer = Color(0xFF3438A4),
    secondaryContainer = Color(0xFF004E6A)
)

@Composable
fun WizardQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
