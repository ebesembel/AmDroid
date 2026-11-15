package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                .height(18.dp)
                .background(WinampEqSlot, RoundedCornerShape(2.dp))
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
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { deltaPx ->
                        if (size.width > 0) {
                            val deltaFraction = deltaPx / size.width
                            val newVal = (value + deltaFraction).coerceIn(0f, 1f)
                            onValueChange(newVal)
                        }
                    }
                )
        ) {
            // Track center groove
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.Center)
                    .background(Color.Black)
            )

            // Metallic Thumb (Enlarged for touch comfort)
            if (size.width > 0) {
                val thumbWidth = 26.dp
                val thumbOffset = ((size.width - thumbWidth.value * 2) * value).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .width(thumbWidth)
                        .height(16.dp)
                        .offset(x = thumbOffset.dp)
                        .align(Alignment.CenterStart)
                        .background(WinampEqThumb, RoundedCornerShape(2.dp))
                        .border(1.dp, WinampBorderHighlight)
                ) {
                    // Bevel grip line
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(10.dp)
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
    valueDb: Float, // -15.0f to +15.0f
    onValueChange: (Float) -> Unit,
    frequencyLabel: String,
    onStepUp: () -> Unit,
    onStepDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var heightPx by remember { mutableIntStateOf(0) }
    val maxDb = 15.0f

    Column(
        modifier = modifier
            .width(42.dp)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frequency label at top
        Text(
            text = frequencyLabel,
            color = WinampLcdGreen,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Step UP button (+1 dB) for precision tapping
        Box(
            modifier = Modifier
                .size(24.dp, 16.dp)
                .background(WinampButtonFace, RoundedCornerShape(1.dp))
                .border(1.dp, WinampBorderHighlight)
                .clickable { onStepUp() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▲",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Vertical track with enlarged touch target
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(105.dp)
                .background(WinampEqSlot, RoundedCornerShape(2.dp))
                .border(1.dp, WinampBorderShadow)
                .onSizeChanged { heightPx = it.height }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            onValueChange(0f) // Double tap resets to 0dB
                        },
                        onTap = { offset ->
                            if (heightPx > 0) {
                                val fraction = (offset.y / heightPx).coerceIn(0f, 1f)
                                val db = maxDb - (fraction * (maxDb * 2))
                                onValueChange(db)
                            }
                        }
                    )
                }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { deltaPx ->
                        if (heightPx > 0) {
                            // Dragging down increases Y (decreases dB)
                            val deltaFraction = deltaPx / heightPx
                            val deltaDb = -deltaFraction * (maxDb * 2)
                            val newDb = (valueDb + deltaDb).coerceIn(-maxDb, maxDb)
                            onValueChange(newDb)
                        }
                    }
                )
        ) {
            // Center 0dB mark line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFF666666))
            )

            // Metallic Fader Knob (wider for mobile thumbs)
            if (heightPx > 0) {
                val fraction = ((maxDb - valueDb) / (maxDb * 2)).coerceIn(0f, 1f)
                val knobHeight = 16.dp
                val knobOffset = ((heightPx - knobHeight.value * 2) * fraction).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(knobHeight)
                        .offset(y = knobOffset.dp)
                        .align(Alignment.TopCenter)
                        .background(WinampEqThumb, RoundedCornerShape(2.dp))
                        .border(1.dp, WinampBorderHighlight)
                ) {
                    // Center grip groove on knob
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(2.dp)
                            .align(Alignment.Center)
                            .background(WinampMetalDark)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Step DOWN button (-1 dB) for precision tapping
        Box(
            modifier = Modifier
                .size(24.dp, 16.dp)
                .background(WinampButtonFace, RoundedCornerShape(1.dp))
                .border(1.dp, WinampBorderHighlight)
                .clickable { onStepDown() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▼",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Current dB value
        Text(
            text = String.format("%+.1f", valueDb),
            color = if (valueDb > 0) WinampLcdGreen else if (valueDb < 0) WinampLcdOrange else Color.Gray,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
