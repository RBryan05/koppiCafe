package com.grupo5.cafeteriaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de colores para modo claro; tonos café cálidos como color principal
private val LightColors = lightColorScheme(
    primary = Color(0xFF6D4C41),       // Café oscuro
    onPrimary = Color.White,
    background = Color(0xFFFFF8F5),    // Blanco cálido
    onBackground = Color(0xFF1A1A1A),  // Casi negro para texto
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    secondary = Color(0xFFD7A86E),     // Café claro/dorado
    onSecondary = Color.White
)

// Paleta de colores para modo oscuro; misma identidad pero adaptada para fondos oscuros
private val DarkColors = darkColorScheme(
    primary = Color(0xFFBCAAA4),       // Café desaturado para contraste sobre oscuro
    onPrimary = Color(0xFF3E2723),     // Café muy oscuro
    background = Color(0xFF121212),    // Negro estándar de Material Dark
    onBackground = Color(0xFFE0E0E0),  // Gris claro para texto
    surface = Color(0xFF1E1E1E),       // Superficie ligeramente más clara que el fondo
    onSurface = Color(0xFFE0E0E0),
    secondary = Color(0xFFD7A86E),     // Mismo dorado que en modo claro
    onSecondary = Color(0xFF3E2723)
)

// Tema principal de la app; recibe darkTheme para alternar entre paletas
// El parámetro 'content' es el árbol de composables que heredará este tema
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