package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.ui.theme.*

@Composable
fun RetroHorizontalSlider(
    value: Float, // 0.0 to 1.0
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label.uppercase(),
                color = WinampLcdDimGreen,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(WinampEqSlot, RoundedCornerShape(1.dp))
                .border(1.dp, WinampBorderShadow)
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (size.width > 0) {
                            val newVal = (offset.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newVal)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        if (size.width > 0) {
                            val newVal = (change.position.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newVal)
                        }
                    }
                }
        ) {
            // Track center groove
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .background(Color.Black)
            )

            // Metallic Thumb
            if (size.width > 0) {
                val thumbWidth = 24.dp
                val thumbOffset = ((size.width - thumbWidth.value * 2) * value).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .width(thumbWidth)
                        .height(12.dp)
                        .offset(x = thumbOffset.dp)
                        .align(Alignment.CenterStart)
                        .background(WinampEqThumb, RoundedCornerShape(1.dp))
                        .border(1.dp, WinampBorderHighlight)
                ) {
                    // Bevel grip line
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(8.dp)
                            .align(Alignment.Center)
                            .background(WinampMetalDark)
                    )
                }
            }
        }
    }
}

@Composable
fun RetroVerticalSlider(
    valueDb: Float, // -12.0f to +12.0f
    onValueChange: (Float) -> Unit,
    frequencyLabel: String,
    modifier: Modifier = Modifier
) {
    var heightPx by remember { mutableStateOf(0) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frequency label at top
        Text(
            text = frequencyLabel,
            color = WinampLcdGreen,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Vertical track
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(90.dp)
                .background(WinampEqSlot, RoundedCornerShape(1.dp))
                .border(1.dp, WinampBorderShadow)
                .onSizeChanged { heightPx = it.height }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (heightPx > 0) {
                            // top is +12dB, bottom is -12dB
                            val fraction = (offset.y / heightPx).coerceIn(0f, 1f)
                            val db = 12.0f - (fraction * 24.0f)
                            onValueChange(db)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        if (heightPx > 0) {
                            val fraction = (change.position.y / heightPx).coerceIn(0f, 1f)
                            val db = 12.0f - (fraction * 24.0f)
                            onValueChange(db)
                        }
                    }
                }
        ) {
            // Center 0dB mark line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFF444444))
            )

            // Metallic Fader Knob
            if (heightPx > 0) {
                // fraction: 0.0 (+12dB) -> 1.0 (-12dB)
                val fraction = ((12.0f - valueDb) / 24.0f).coerceIn(0f, 1f)
                val knobHeight = 12.dp
                val knobOffset = ((heightPx - knobHeight.value * 2) * fraction).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(knobHeight)
                        .offset(y = knobOffset.dp)
                        .align(Alignment.TopCenter)
                        .background(WinampEqThumb, RoundedCornerShape(1.dp))
                        .border(1.dp, WinampBorderHighlight)
                ) {
                    // Center grip groove on knob
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(1.dp)
                            .align(Alignment.Center)
                            .background(WinampBorderShadow)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Current dB value
        Text(
            text = String.format("%+.0f", valueDb),
            color = WinampLcdDimGreen,
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
