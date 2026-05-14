package com.grupo5.cafeteriaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6D4C41),
    onPrimary = Color.White,
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    secondary = Color(0xFFD7A86E),
    onSecondary = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBCAAA4),
    onPrimary = Color(0xFF3E2723),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    secondary = Color(0xFFD7A86E),
    onSecondary = Color(0xFF3E2723)
)

@Composable
fun CafeteriaAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}