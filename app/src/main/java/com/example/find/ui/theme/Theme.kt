package com.example.find.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Leaf.copy(alpha = 0.35f),
    onPrimaryContainer = ForestDark,
    secondary = Moss,
    onSecondary = Color.White,
    secondaryContainer = Fog,
    onSecondaryContainer = Bark,
    tertiary = AmberStar,
    onTertiary = Bark,
    background = Mist,
    onBackground = Bark,
    surface = Color.White,
    onSurface = Bark,
    surfaceVariant = Fog,
    onSurfaceVariant = Stone,
    outline = Stone.copy(alpha = 0.35f),
    error = SoftError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Leaf,
    onPrimary = ForestDark,
    primaryContainer = Forest,
    onPrimaryContainer = Color.White,
    secondary = Moss,
    onSecondary = Color.White,
    background = Color(0xFF121A16),
    onBackground = Color(0xFFE8F0EB),
    surface = Color(0xFF1A2420),
    onSurface = Color(0xFFE8F0EB),
    surfaceVariant = Color(0xFF24302B),
    onSurfaceVariant = Color(0xFFB7C7BE),
    error = SoftError,
    onError = Color.White
)

@Composable
fun FindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
