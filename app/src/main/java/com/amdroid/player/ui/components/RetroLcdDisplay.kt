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
                targetValue = -350f,
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
            .height(72.dp)
            .background(AmDroidLcdBg, RoundedCornerShape(3.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(3.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

            // Top Row: Big LED Digital Time & Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Digital 7-segment style counter in Electric Cyan
                Row(
                    modifier = Modifier.clickable { showRemainingTime = !showRemainingTime },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayTime,
                        color = AmDroidCyanBright,
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showRemainingTime) "REST" else "TRAN",
                        color = AmDroidLcdDimCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // KBPS & KHZ badges
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        Text(
                            text = "$bitrateKbps",
                            color = AmDroidCyanGlow,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " kbps",
                            color = AmDroidLcdDimCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row {
                        Text(
                            text = "$sampleRateKhz",
                            color = AmDroidCyanGlow,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " kHz",
                            color = AmDroidLcdDimCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Bottom Row: Scrolling Track Name & ESTÉREO badge
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
                        text = if (trackTitle.isBlank()) "AmDroid - Listo para sonar" else trackTitle,
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.offset(x = scrollOffset.value.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Estéreo / Mono Badge
                Row {
                    Text(
                        text = "MONO",
                        color = AmDroidLcdDimCyan,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ESTÉREO",
                        color = if (isPlaying) AmDroidCyanBright else AmDroidLcdDimCyan,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
