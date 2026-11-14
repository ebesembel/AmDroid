package com.amdroid.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.amdroid.player.audio.VisualizerMode
import com.amdroid.player.ui.theme.*

@Composable
fun SpectrumVisualizerView(
    mode: VisualizerMode,
    bars: FloatArray,
    peaks: FloatArray,
    waves: FloatArray,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(WinampLcdBackground, RoundedCornerShape(2.dp))
            .border(1.dp, WinampBorderShadow)
            .clickable { onToggleMode() }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            val canvasW = size.width
            val canvasH = size.height

            when (mode) {
                VisualizerMode.SPECTRUM -> {
                    val barCount = bars.size
                    val spacing = 2.dp.toPx()
                    val totalSpacing = spacing * (barCount + 1)
                    val barW = (canvasW - totalSpacing) / barCount

                    for (i in 0 until barCount) {
                        val barH = (bars[i] * canvasH).coerceIn(0f, canvasH)
                        val peakH = (peaks[i] * canvasH).coerceIn(0f, canvasH)

                        val left = spacing + i * (barW + spacing)
                        val top = canvasH - barH

                        // Draw segmented colored bar (Winamp Green -> Yellow -> Red)
                        val segments = 12
                        val segmentHeight = canvasH / segments
                        val activeSegments = (barH / segmentHeight).toInt()

                        for (s in 0 until activeSegments) {
                            val segTop = canvasH - (s + 1) * segmentHeight
                            val segColor = when {
                                s > 9 -> Color(0xFFFF2200) // Red high peak
                                s > 6 -> Color(0xFFFFDD00) // Yellow mid peak
                                else -> Color(0xFF00FF00)  // Green base
                            }

                            drawRect(
                                color = segColor,
                                topLeft = Offset(left, segTop + 1f),
                                size = Size(barW, segmentHeight - 1.5f)
                            )
                        }

                        // Draw Peak falling dot
                        if (peakH > 2f) {
                            val peakTop = (canvasH - peakH).coerceIn(0f, canvasH - 3f)
                            drawRect(
                                color = Color(0xFFFFFFFF),
                                topLeft = Offset(left, peakTop),
                                size = Size(barW, 2f)
                            )
                        }
                    }
                }

                VisualizerMode.OSCILLOSCOPE -> {
                    if (waves.isNotEmpty()) {
                        val path = Path()
                        val step = canvasW / (waves.size - 1)
                        val centerY = canvasH / 2f

                        for (i in waves.indices) {
                            val x = i * step
                            val y = centerY + (waves[i] * (canvasH / 2f - 4f))
                            if (i == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = WinampLcdGreen,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                VisualizerMode.OFF -> {
                    // Blank retro screen
                }
            }
        }
    }
}
