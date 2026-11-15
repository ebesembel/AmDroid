package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
        title = "AmDroid - Ecualizador Gráfico",
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
                    label = if (isEqEnabled) "ENC" else "APA",
                    isActive = isEqEnabled,
                    onToggle = { onToggleEq(!isEqEnabled) }
                )

                RetroButton(
                    text = "PREAJUSTES",
                    onClick = { showPresetsDialog = true },
                    width = 90.dp,
                    height = 26.dp
                )
            }

            RetroButton(
                text = "REINICIAR",
                onClick = {
                    onPreampChange(0f)
                    for (i in 0 until 10) onBandChange(i, 0f)
                },
                width = 80.dp,
                height = 26.dp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontally scrollable row containing PREAMP + 10 BANDS with comfortable touch width
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF080B11), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp, horizontal = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preamp Slider
            RetroVerticalSlider(
                valueDb = preampDb,
                onValueChange = onPreampChange,
                frequencyLabel = "PREAMP",
                onStepUp = { eqManager.adjustPreamp(1.0f); onPreampChange(eqManager.preampGainDb) },
                onStepDown = { eqManager.adjustPreamp(-1.0f); onPreampChange(eqManager.preampGainDb) }
            )

            // Vertical divider between Preamp and 10 bands
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(130.dp)
                    .background(Color(0xFF0284C7))
            )

            // 10 Frequency Sliders
            val labels = eqManager.classicFrequencies
            for (i in 0 until 10) {
                val db = if (i in bandLevels.indices) bandLevels[i] else 0f
                RetroVerticalSlider(
                    valueDb = db,
                    onValueChange = { newDb -> onBandChange(i, newDb) },
                    frequencyLabel = labels.getOrElse(i) { "$i" },
                    onStepUp = { eqManager.adjustBand(i, 1.0f); onBandChange(i, eqManager.bandLevels[i]) },
                    onStepDown = { eqManager.adjustBand(i, -1.0f); onBandChange(i, eqManager.bandLevels[i]) }
                )
            }
        }
    }

    if (showPresetsDialog) {
        Dialog(onDismissRequest = { showPresetsDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(WinampMetalMid, RoundedCornerShape(4.dp))
                    .border(2.dp, WinampBorderHighlight)
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREAJUSTES DE SONIDO",
                            color = WinampLcdGreen,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { showPresetsDialog = false }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                        items(eqManager.presets) { preset ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onApplyPreset(preset)
                                        showPresetsDialog = false
                                    }
                                    .background(WinampMetalDark, RoundedCornerShape(2.dp))
                                    .border(1.dp, WinampBorderShadow)
                                    .padding(vertical = 10.dp, horizontal = 10.dp)
                            ) {
                                Text(
                                    text = preset.name,
                                    color = if (preset.name.contains("Bajos & Agudos")) WinampLcdGreen else Color.White,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
