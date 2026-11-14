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
        title = "AmDroid v1.0 - Main Window",
        modifier = modifier
    ) {
        // 1. Retro LCD Panel
        RetroLcdDisplay(
            trackTitle = currentTrack?.let { "${it.artist} - ${it.title}" } ?: "AmDroid - Ready to Rock",
            currentPosMs = currentPosMs,
            totalDurationMs = currentTrack?.durationMs ?: 0L,
            isPlaying = isPlaying,
            bitrateKbps = currentTrack?.bitrateKbps ?: 320,
            sampleRateKhz = currentTrack?.sampleRateKhz ?: 44
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Real-time Spectrum Analyzer & Oscilloscope
        SpectrumVisualizerView(
            mode = visualizerMode,
            bars = bars,
            peaks = peaks,
            waves = waves,
            onToggleMode = onToggleVisualizer
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Song Progress Seeker
        val progressFraction = if ((currentTrack?.durationMs ?: 0L) > 0) {
            (currentPosMs.toFloat() / currentTrack!!.durationMs).coerceIn(0f, 1f)
        } else 0f

        RetroHorizontalSlider(
            value = progressFraction,
            onValueChange = onSeek,
            label = "SEEK"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Volume and Balance Sliders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RetroHorizontalSlider(
                value = volume,
                onValueChange = onVolumeChange,
                label = "VOLUME",
                modifier = Modifier.weight(1f)
            )

            // Balance: convert -1..+1 to 0..1 for the slider
            val balFraction = (balance + 1.0f) / 2.0f
            RetroHorizontalSlider(
                value = balFraction,
                onValueChange = { frac ->
                    val pan = frac * 2.0f - 1.0f
                    onBalanceChange(pan)
                },
                label = "BALANCE (L-R)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Classic Playback Controls Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main playback keys
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RetroButton(text = "|<<", onClick = onPrev)
                RetroButton(text = if (isPlaying) "||" else ">", onClick = onPlayPause, isActive = isPlaying)
                RetroButton(text = "■", onClick = onStop)
                RetroButton(text = ">>|", onClick = onNext)
                RetroButton(text = "⏏", onClick = onOpenAudioPicker)
            }

            // Window and mode toggles
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RetroToggleBadge(label = "EQ", isActive = showEqualizer, onToggle = onToggleEqualizer)
                RetroToggleBadge(label = "PL", isActive = showPlaylist, onToggle = onTogglePlaylist)
                RetroToggleBadge(label = "SHUF", isActive = isShuffle, onToggle = onToggleShuffle)
                RetroToggleBadge(label = "REP", isActive = isRepeat, onToggle = onToggleRepeat)
            }
        }
    }
}
