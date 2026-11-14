package com.amdroid.player.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.ui.theme.*

@Composable
fun RetroLcdDisplay(
    trackTitle: String,
    currentPosMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    bitrateKbps: Int = 320,
    sampleRateKhz: Int = 44,
    modifier: Modifier = Modifier
) {
    var showRemainingTime by remember { mutableStateOf(false) }

    // Marquee scrolling for track title
    val scrollOffset = remember { Animatable(0f) }
    LaunchedEffect(trackTitle, isPlaying) {
        if (isPlaying && trackTitle.length > 20) {
            scrollOffset.snapTo(0f)
            scrollOffset.animateTo(
                targetValue = -300f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            scrollOffset.snapTo(0f)
        }
    }

    val displayTime = remember(currentPosMs, totalDurationMs, showRemainingTime) {
        val targetMs = if (showRemainingTime) {
            (totalDurationMs - currentPosMs).coerceAtLeast(0L)
        } else {
            currentPosMs
        }
        val totalSec = targetMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        val sign = if (showRemainingTime) "-" else " "
        String.format("%s%02d:%02d", sign, min, sec)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(WinampLcdBackground, RoundedCornerShape(2.dp))
            .border(1.dp, WinampBorderShadow)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

            // Top Row: Big LED Digital Time & Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Digital 7-segment style counter
                Row(
                    modifier = Modifier.clickable { showRemainingTime = !showRemainingTime },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayTime,
                        color = WinampLcdGreen,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showRemainingTime) "REM" else "ELAP",
                        color = WinampLcdDimGreen,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // KBPS & KHZ badges
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        Text(
                            text = "$bitrateKbps",
                            color = WinampLcdGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " kbps",
                            color = WinampLcdDimGreen,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row {
                        Text(
                            text = "$sampleRateKhz",
                            color = WinampLcdGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " kHz",
                            color = WinampLcdDimGreen,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Bottom Row: Scrolling Track Name & STEREO badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds()
                ) {
                    Text(
                        text = if (trackTitle.isBlank()) "AmDroid - Ready to Play" else trackTitle,
                        color = WinampLcdGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.offset(x = scrollOffset.value.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Stereo / Mono Badge
                Row {
                    Text(
                        text = "MONO",
                        color = WinampLcdDimGreen,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "STEREO",
                        color = if (isPlaying) WinampLcdOrange else WinampLcdDimGreen,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
