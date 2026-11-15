package com.amdroid.player.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amdroid.player.audio.VisualizerMode
import com.amdroid.player.model.Track

@Composable
fun MainWindow(
    currentTrack: Track?,
    isPlaying: Boolean,
    currentPosMs: Long,
    visualizerMode: VisualizerMode,
    bars: FloatArray,
    peaks: FloatArray,
    waves: FloatArray,
    volume: Float,
    balance: Float,
    isShuffle: Boolean,
    isRepeat: Boolean,
    showEqualizer: Boolean,
    showPlaylist: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBalanceChange: (Float) -> Unit,
    onToggleVisualizer: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleEqualizer: () -> Unit,
    onTogglePlaylist: () -> Unit,
    onOpenAudioPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    RetroWindowBox(
        title = "AmDroid Pro - Ventana Principal",
        modifier = modifier
    ) {
        // 1. Pantalla LCD Retro
        RetroLcdDisplay(
            trackTitle = currentTrack?.let { "${it.artist} - ${it.title}" } ?: "AmDroid - Listo para sonar",
            currentPosMs = currentPosMs,
            totalDurationMs = currentTrack?.durationMs ?: 0L,
            isPlaying = isPlaying,
            bitrateKbps = currentTrack?.bitrateKbps ?: 320,
            sampleRateKhz = currentTrack?.sampleRateKhz ?: 44
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Analizador de Espectro y Osciloscopio en tiempo real
        SpectrumVisualizerView(
            mode = visualizerMode,
            bars = bars,
            peaks = peaks,
            waves = waves,
            onToggleMode = onToggleVisualizer
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Deslizador de Posición del Tema
        val progressFraction = if ((currentTrack?.durationMs ?: 0L) > 0) {
            (currentPosMs.toFloat() / currentTrack!!.durationMs).coerceIn(0f, 1f)
        } else 0f

        RetroHorizontalSlider(
            value = progressFraction,
            onValueChange = onSeek,
            label = "POSICIÓN DEL TEMA"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Deslizadores de Volumen y Balance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RetroHorizontalSlider(
                value = volume,
                onValueChange = onVolumeChange,
                label = "VOLUMEN",
                modifier = Modifier.weight(1f)
            )

            // Balance: convertir -1..+1 a 0..1
            val balFraction = (balance + 1.0f) / 2.0f
            RetroHorizontalSlider(
                value = balFraction,
                onValueChange = { frac ->
                    val pan = frac * 2.0f - 1.0f
                    onBalanceChange(pan)
                },
                label = "BALANCE (I-D)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Botonera Clásica de Reproducción y Modos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Controles de transporte
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RetroButton(text = "|<<", onClick = onPrev, width = 38.dp, height = 30.dp)
                RetroButton(text = if (isPlaying) "||" else ">", onClick = onPlayPause, isActive = isPlaying, width = 42.dp, height = 30.dp)
                RetroButton(text = "■", onClick = onStop, width = 36.dp, height = 30.dp)
                RetroButton(text = ">>|", onClick = onNext, width = 38.dp, height = 30.dp)
                RetroButton(text = "⏏", onClick = onOpenAudioPicker, width = 36.dp, height = 30.dp)
            }

            // Interruptores de Ventanas y Modos
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                RetroToggleBadge(label = "ECUAL", isActive = showEqualizer, onToggle = onToggleEqualizer)
                RetroToggleBadge(label = "LISTA", isActive = showPlaylist, onToggle = onTogglePlaylist)
                RetroToggleBadge(label = "ALEAT", isActive = isShuffle, onToggle = onToggleShuffle)
                RetroToggleBadge(label = "REPET", isActive = isRepeat, onToggle = onToggleRepeat)
            }
        }
    }
}
