package com.amdroid.player.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WinampLcdGreen,
    secondary = WinampLcdOrange,
    background = WinampMetalDark,
    surface = WinampMetalMid,
    onPrimary = WinampMetalDark,
    onSecondary = WinampMetalDark,
    onBackground = WinampLcdGreen,
    onSurface = WinampLcdGreen
)

@Composable
fun AmDroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
