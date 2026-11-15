package com.amdroid.player.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AmDroidCyanGlow,
    secondary = AmDroidBlueElectric,
    background = AmDroidMetalDark,
    surface = AmDroidMetalMid,
    onPrimary = AmDroidMetalDark,
    onSecondary = AmDroidTextBright,
    onBackground = AmDroidCyanGlow,
    onSurface = AmDroidTextBright
)

@Composable
fun AmDroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
