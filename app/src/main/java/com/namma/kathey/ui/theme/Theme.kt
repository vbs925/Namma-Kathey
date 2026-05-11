package com.namma.kathey.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Saffron   = Color(0xFFFF6F00)
val DeepGreen = Color(0xFF1B5E20)
val NavyBlue  = Color(0xFF1A237E)
val Gold      = Color(0xFFFFC107)
val LightBg   = Color(0xFFFFF8F0)
val White     = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = Saffron, onPrimary = White,
    primaryContainer = Color(0xFFFFE0B2),
    secondary = DeepGreen, onSecondary = White,
    tertiary = NavyBlue,
    background = LightBg, surface = White,
    onBackground = Color(0xFF3E2723), onSurface = Color(0xFF3E2723)
)
private val DarkColors = darkColorScheme(
    primary = Gold, onPrimary = Color(0xFF3E2723),
    primaryContainer = Saffron, secondary = Color(0xFF81C784),
    background = Color(0xFF1C1B1F), surface = Color(0xFF2C2C2C),
    onBackground = Color(0xFFF5E6D3), onSurface = Color(0xFFF5E6D3)
)

@Composable
fun NammaKatheyTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
