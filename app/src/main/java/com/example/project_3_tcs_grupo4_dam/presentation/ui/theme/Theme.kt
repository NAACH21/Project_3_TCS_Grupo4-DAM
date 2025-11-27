package com.example.project_3_tcs_grupo4_dam.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Simple color definitions (you can replace with your brand colors later)
private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF0A63C2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E5FF), // Azul claro para el contenedor primario
    onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF0E4F9C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCDE5FF),
    onSecondaryContainer = Color(0xFF001D36),
    background = Color(0xFFF6F7FB),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF0A63C2)
)

@Composable
fun Project_3_TCS_Grupo4DAMTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
