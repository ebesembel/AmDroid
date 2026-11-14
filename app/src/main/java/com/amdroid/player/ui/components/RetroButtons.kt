package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.ui.theme.*

@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    width: Dp = 36.dp,
    height: Dp = 26.dp
) {
    val bg = if (isActive) WinampBorderHighlight else WinampButtonFace
    val borderCol = if (isActive) WinampLcdGreen else WinampBorderHighlight

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(bg, RoundedCornerShape(2.dp))
            .border(1.dp, borderCol)
            .border(2.dp, WinampBorderShadow)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) WinampLcdGreen else Color.White,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RetroToggleBadge(
    label: String,
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (isActive) WinampLcdDimGreen else WinampMetalDark, RoundedCornerShape(2.dp))
            .border(1.dp, if (isActive) WinampLcdGreen else WinampBorderShadow)
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = if (isActive) WinampLcdGreen else WinampLcdDimGreen,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
