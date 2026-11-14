package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.amdroid.player.audio.EqPreset
import com.amdroid.player.audio.EqualizerManager
import com.amdroid.player.ui.theme.*

@Composable
fun EqualizerWindow(
    eqManager: EqualizerManager,
    isEqEnabled: Boolean,
    preampDb: Float,
    bandLevels: FloatArray,
    onToggleEq: (Boolean) -> Unit,
    onPreampChange: (Float) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onApplyPreset: (EqPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPresetsDialog by remember { mutableStateOf(false) }

    RetroWindowBox(
        title = "AmDroid - Equalizer",
        modifier = modifier
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RetroToggleBadge(
                    label = if (isEqEnabled) "ON" else "OFF",
                    isActive = isEqEnabled,
                    onToggle = { onToggleEq(!isEqEnabled) }
                )

                RetroButton(
                    text = "PRESETS",
                    onClick = { showPresetsDialog = true },
                    width = 70.dp,
                    height = 24.dp
                )
            }

            RetroButton(
                text = "FLAT",
                onClick = {
                    onPreampChange(0f)
                    for (i in 0 until 10) onBandChange(i, 0f)
                },
                width = 50.dp,
                height = 24.dp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sliders Row: PREAMP + 10 BANDS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WinampMetalDark, RoundedCornerShape(2.dp))
                .border(1.dp, WinampBorderShadow)
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preamp Slider
            RetroVerticalSlider(
                valueDb = preampDb,
                onValueChange = onPreampChange,
                frequencyLabel = "PRE"
            )

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(90.dp)
                    .background(WinampBorderHighlight)
            )

            // 10 Frequency Sliders
            val labels = eqManager.classicFrequencies
            for (i in 0 until 10) {
                val db = if (i in bandLevels.indices) bandLevels[i] else 0f
                RetroVerticalSlider(
                    valueDb = db,
                    onValueChange = { newDb -> onBandChange(i, newDb) },
                    frequencyLabel = labels.getOrElse(i) { "$i" }
                )
            }
        }
    }

    if (showPresetsDialog) {
        Dialog(onDismissRequest = { showPresetsDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(WinampMetalMid, RoundedCornerShape(4.dp))
                    .border(2.dp, WinampBorderHighlight)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "SELECT EQUALIZER PRESET",
                        color = WinampLcdGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(eqManager.presets) { preset ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onApplyPreset(preset)
                                        showPresetsDialog = false
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = preset.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
