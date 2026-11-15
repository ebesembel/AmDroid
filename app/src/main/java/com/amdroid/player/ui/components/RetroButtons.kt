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
    width: Dp = 38.dp,
    height: Dp = 28.dp
) {
    val bg = if (isActive) AmDroidBlueDeep else AmDroidButtonFace
    val borderCol = if (isActive) AmDroidCyanBright else AmDroidBorderHighlight

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(bg, RoundedCornerShape(3.dp))
            .border(1.dp, borderCol, RoundedCornerShape(3.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Color.White else Color(0xFFE2E8F0),
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
    val bg = if (isActive) AmDroidBlueElectric else AmDroidMetalDark
    val borderCol = if (isActive) AmDroidCyanGlow else Color(0xFF1E293B)
    val textCol = if (isActive) Color.White else AmDroidTextMuted

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(3.dp))
            .border(1.dp, borderCol, RoundedCornerShape(3.dp))
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = textCol,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
