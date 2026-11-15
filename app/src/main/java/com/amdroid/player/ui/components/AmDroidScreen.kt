package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amdroid.player.audio.VisualizerMode
import com.amdroid.player.service.AudioPlaybackService
import com.amdroid.player.ui.theme.AmDroidMetalDark

@Composable
fun AmDroidScreen(
    service: AudioPlaybackService?,
    onOpenAudioPicker: () -> Unit
) {
    if (service == null) return

    val playlist by service.playlist.collectAsState()
    val currentIdx by service.currentTrackIndex.collectAsState()
    val isPlaying by service.isPlaying.collectAsState()
    val currentPosMs by service.currentPositionMs.collectAsState()
    val isShuffle by service.isShuffle.collectAsState()
    val isRepeat by service.isRepeat.collectAsState()

    var showEqualizer by remember { mutableStateOf(true) }
    var showPlaylist by remember { mutableStateOf(true) }

    var masterVolume by remember { mutableFloatStateOf(1.0f) }
    var panBalance by remember { mutableFloatStateOf(0.0f) }

    var visualizerMode by remember { mutableStateOf(VisualizerMode.SPECTRUM) }
    var currentSortMode by remember { mutableStateOf(PlaylistSortMode.DEFAULT) }

    // Equalizer state triggers
    var eqEnabled by remember { mutableStateOf(service.equalizerManager.isEnabled) }
    var preampDb by remember { mutableFloatStateOf(service.equalizerManager.preampGainDb) }
    var eqRecomposeKey by remember { mutableIntStateOf(0) }

    val currentTrack = if (currentIdx in playlist.indices) playlist[currentIdx] else null

    // Sorted playlist based on currentSortMode
    val displayedTracks = remember(playlist, currentSortMode) {
        when (currentSortMode) {
            PlaylistSortMode.FOLDER -> playlist.sortedWith(
                compareBy<com.amdroid.player.model.Track> { it.folderName.lowercase() }
                    .thenBy { it.title.lowercase() }
            )
            PlaylistSortMode.ALBUM -> playlist.sortedWith(
                compareBy<com.amdroid.player.model.Track> { it.album.lowercase() }
                    .thenBy { it.title.lowercase() }
            )
            PlaylistSortMode.DEFAULT -> playlist
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmDroidMetalDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Classic Main Player Window (Sleek Studio Redesign)
            MainWindow(
                currentTrack = currentTrack,
                isPlaying = isPlaying,
                currentPosMs = currentPosMs,
                visualizerMode = visualizerMode,
                bars = service.visualizerManager.barValues,
                peaks = service.visualizerManager.peakValues,
                waves = service.visualizerManager.wavePoints,
                volume = masterVolume,
                balance = panBalance,
                isShuffle = isShuffle,
                isRepeat = isRepeat,
                showEqualizer = showEqualizer,
                showPlaylist = showPlaylist,
                onPlayPause = { service.togglePlayPause() },
                onStop = { service.stop() },
                onNext = { service.next() },
                onPrev = { service.previous() },
                onSeek = { frac ->
                    currentTrack?.let {
                        val targetMs = (it.durationMs * frac).toLong()
                        service.seekTo(targetMs)
                    }
                },
                onVolumeChange = { vol ->
                    masterVolume = vol
                    service.setVolume(vol)
                },
                onBalanceChange = { bal ->
                    panBalance = bal
                    service.setBalance(bal)
                },
                onToggleVisualizer = {
                    visualizerMode = when (visualizerMode) {
                        VisualizerMode.SPECTRUM -> VisualizerMode.OSCILLOSCOPE
                        VisualizerMode.OSCILLOSCOPE -> VisualizerMode.OFF
                        VisualizerMode.OFF -> VisualizerMode.SPECTRUM
                    }
                    service.visualizerManager.mode = visualizerMode
                },
                onToggleShuffle = { service.toggleShuffle() },
                onToggleRepeat = { service.toggleRepeat() },
                onToggleEqualizer = { showEqualizer = !showEqualizer },
                onTogglePlaylist = { showPlaylist = !showPlaylist },
                onOpenAudioPicker = onOpenAudioPicker
            )

            // 2. Graphic Equalizer Window (Toggleable)
            if (showEqualizer) {
                key(eqRecomposeKey) {
                    EqualizerWindow(
                        eqManager = service.equalizerManager,
                        isEqEnabled = eqEnabled,
                        preampDb = preampDb,
                        bandLevels = service.equalizerManager.bandLevels,
                        onToggleEq = { enabled ->
                            eqEnabled = enabled
                            service.equalizerManager.setEnabled(enabled)
                            eqRecomposeKey++
                        },
                        onPreampChange = { db ->
                            preampDb = db
                            service.equalizerManager.setPreamp(db)
                            eqRecomposeKey++
                        },
                        onBandChange = { band, db ->
                            service.equalizerManager.setBandLevel(band, db)
                            eqRecomposeKey++
                        },
                        onApplyPreset = { preset ->
                            service.equalizerManager.applyPreset(preset)
                            preampDb = service.equalizerManager.preampGainDb
                            eqRecomposeKey++
                        }
                    )
                }
            }

            // 3. Playlist Window (Toggleable with Sorting Modes)
            if (showPlaylist) {
                // Find index of currently playing song in displayed list
                val displayedCurrentIdx = if (currentTrack != null) {
                    displayedTracks.indexOfFirst { it.id == currentTrack.id }
                } else -1

                PlaylistWindow(
                    tracks = displayedTracks,
                    currentTrackIndex = displayedCurrentIdx,
                    currentSortMode = currentSortMode,
                    onSortModeChanged = { mode ->
                        currentSortMode = mode
                        if (mode != PlaylistSortMode.DEFAULT) {
                            val newSorted = when (mode) {
                                PlaylistSortMode.FOLDER -> playlist.sortedWith(
                                    compareBy<com.amdroid.player.model.Track> { it.folderName.lowercase() }
                                        .thenBy { it.title.lowercase() }
                                )
                                PlaylistSortMode.ALBUM -> playlist.sortedWith(
                                    compareBy<com.amdroid.player.model.Track> { it.album.lowercase() }
                                        .thenBy { it.title.lowercase() }
                                )
                                else -> playlist
                            }
                            service.setPlaylist(newSorted, startIdx = if (currentTrack != null) newSorted.indexOfFirst { it.id == currentTrack.id }.coerceAtLeast(0) else 0)
                        }
                    },
                    onShuffleOrder = {
                        val shuffled = playlist.shuffled()
                        val newIdx = if (currentTrack != null) shuffled.indexOfFirst { it.id == currentTrack.id }.coerceAtLeast(0) else 0
                        service.setPlaylist(shuffled, startIdx = newIdx)
                    },
                    onTrackSelected = { selectedIdx ->
                        if (selectedIdx in displayedTracks.indices) {
                            val targetTrack = displayedTracks[selectedIdx]
                            val originalIdx = playlist.indexOfFirst { it.id == targetTrack.id }
                            if (originalIdx >= 0) {
                                service.playTrack(originalIdx)
                            } else {
                                service.playTrack(selectedIdx)
                            }
                        }
                    },
                    onAddAudioClick = onOpenAudioPicker,
                    onClearPlaylist = { service.setPlaylist(emptyList()) }
                )
            }
        }
    }
}
