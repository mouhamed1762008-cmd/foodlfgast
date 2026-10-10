package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern obsidian dark color scheme — Light mode removed per specification
private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = BrandAccent,
    onSecondary = Color(0xFF14151C),
    secondaryContainer = Color(0xFF242736),
    onSecondaryContainer = Color.White,
    tertiary = BrandAccent,
    onTertiary = Color(0xFF14151C),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainer = DarkSurfaceContainer,
    error = BrandError,
    onError = Color.White
)

@Composable
fun QuickBiteTheme(
    darkTheme: Boolean = true, // Light mode removed: always modern dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

