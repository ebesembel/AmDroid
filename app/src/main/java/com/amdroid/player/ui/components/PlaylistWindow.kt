package com.amdroid.player.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.model.Track
import com.amdroid.player.ui.theme.*

enum class PlaylistSortMode {
    DEFAULT,
    FOLDER,
    ALBUM
}

@Composable
fun PlaylistWindow(
    tracks: List<Track>,
    currentTrackIndex: Int,
    onTrackSelected: (Int) -> Unit,
    onAddAudioClick: () -> Unit,
    onClearPlaylist: () -> Unit,
    onShuffleOrder: () -> Unit,
    onSortModeChanged: (PlaylistSortMode) -> Unit,
    currentSortMode: PlaylistSortMode = PlaylistSortMode.DEFAULT,
    modifier: Modifier = Modifier
) {
    // Current playing track's folder and album
    val activeTrack = if (currentTrackIndex in tracks.indices) tracks[currentTrackIndex] else null
    val activeFolderName = activeTrack?.folderName
    val activeAlbumName = activeTrack?.album

    // State for expanded folders (only the active one expanded by default)
    var expandedFolders by remember { mutableStateOf<Set<String>>(emptySet()) }
    var expandedAlbums by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Sync expanded folder whenever active track or sort mode changes
    LaunchedEffect(activeFolderName, currentSortMode) {
        if (activeFolderName != null) {
            expandedFolders = setOf(activeFolderName)
        }
    }
    LaunchedEffect(activeAlbumName, currentSortMode) {
        if (activeAlbumName != null) {
            expandedAlbums = setOf(activeAlbumName)
        }
    }

    RetroWindowBox(
        title = "AmDroid - Lista de Reproducción",
        modifier = modifier
    ) {
        // Barra de Ordenamiento (Carpetas, Álbum, Título, Mezclar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmDroidMetalDark, RoundedCornerShape(2.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SortPill(
                    label = "TITULO",
                    icon = "📋",
                    isSelected = currentSortMode == PlaylistSortMode.DEFAULT,
                    onClick = { onSortModeChanged(PlaylistSortMode.DEFAULT) }
                )
                SortPill(
                    label = "CARPETAS",
                    icon = "📁",
                    isSelected = currentSortMode == PlaylistSortMode.FOLDER,
                    onClick = { onSortModeChanged(PlaylistSortMode.FOLDER) }
                )
                SortPill(
                    label = "ÁLBUM",
                    icon = "💿",
                    isSelected = currentSortMode == PlaylistSortMode.ALBUM,
                    onClick = { onSortModeChanged(PlaylistSortMode.ALBUM) }
                )
            }

            // Botón Mezclar
            Box(
                modifier = Modifier
                    .background(AmDroidButtonFace, RoundedCornerShape(2.dp))
                    .border(1.dp, AmDroidCyanGlow, RoundedCornerShape(2.dp))
                    .clickable { onShuffleOrder() }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔀 MEZCLAR",
                    color = AmDroidCyanBright,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Ventana de canciones / carpetas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(AmDroidPlaylistBg, RoundedCornerShape(2.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(2.dp))
                .padding(2.dp)
        ) {
            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ LISTA VACÍA - TOCA '+ AGREGAR' PARA CARGAR MÚSICA ]",
                        color = AmDroidLcdDimCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                when (currentSortMode) {
                    // MODO CARPETAS PLEGABLES (Solo expandida la que se está reproduciendo)
                    PlaylistSortMode.FOLDER -> {
                        val folderGroups = remember(tracks) { tracks.groupBy { it.folderName } }

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            folderGroups.forEach { (folderName, folderTracks) ->
                                val isExpanded = folderName in expandedFolders
                                val isCurrentFolder = folderName == activeFolderName

                                item(key = "folder_$folderName") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isCurrentFolder) Color(0xFF16253B) else Color(0xFF111722))
                                            .border(1.dp, if (isCurrentFolder) AmDroidCyanGlow else Color(0xFF1E293B))
                                            .clickable {
                                                expandedFolders = if (isExpanded) {
                                                    expandedFolders - folderName
                                                } else {
                                                    expandedFolders + folderName
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = if (isExpanded) "▼" else "▶",
                                                color = if (isCurrentFolder) AmDroidCyanBright else Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "📁 $folderName",
                                                color = if (isCurrentFolder) AmDroidCyanBright else Color(0xFFE2E8F0),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${folderTracks.size})",
                                                color = AmDroidTextMuted,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        if (isCurrentFolder) {
                                            Text(
                                                text = "SONANDO",
                                                color = AmDroidCyanBright,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (isExpanded) {
                                    items(folderTracks) { track ->
                                        val overallIndex = tracks.indexOf(track)
                                        val isSelected = overallIndex == currentTrackIndex
                                        val rowBg = if (isSelected) AmDroidPlaylistSelected else Color.Transparent
                                        val textColor = if (isSelected) Color.White else Color(0xFFE2E8F0)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(rowBg)
                                                .clickable { onTrackSelected(overallIndex) }
                                                .padding(start = 24.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${track.artist} - ${track.title}",
                                                color = textColor,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = track.formattedDuration,
                                                color = if (isSelected) Color.White else AmDroidLcdDimCyan,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MODO ÁLBUM PLEGABLE (Solo expandido el álbum en reproducción)
                    PlaylistSortMode.ALBUM -> {
                        val albumGroups = remember(tracks) { tracks.groupBy { it.album.ifBlank { "Sin Álbum" } } }

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            albumGroups.forEach { (albumName, albumTracks) ->
                                val isExpanded = albumName in expandedAlbums
                                val isCurrentAlbum = albumName == (activeAlbumName?.ifBlank { "Sin Álbum" })

                                item(key = "album_$albumName") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isCurrentAlbum) Color(0xFF16253B) else Color(0xFF111722))
                                            .border(1.dp, if (isCurrentAlbum) AmDroidCyanGlow else Color(0xFF1E293B))
                                            .clickable {
                                                expandedAlbums = if (isExpanded) {
                                                    expandedAlbums - albumName
                                                } else {
                                                    expandedAlbums + albumName
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = if (isExpanded) "▼" else "▶",
                                                color = if (isCurrentAlbum) AmDroidCyanBright else Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "💿 $albumName",
                                                color = if (isCurrentAlbum) AmDroidCyanBright else Color(0xFFE2E8F0),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${albumTracks.size})",
                                                color = AmDroidTextMuted,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        if (isCurrentAlbum) {
                                            Text(
                                                text = "SONANDO",
                                                color = AmDroidCyanBright,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (isExpanded) {
                                    items(albumTracks) { track ->
                                        val overallIndex = tracks.indexOf(track)
                                        val isSelected = overallIndex == currentTrackIndex
                                        val rowBg = if (isSelected) AmDroidPlaylistSelected else Color.Transparent
                                        val textColor = if (isSelected) Color.White else Color(0xFFE2E8F0)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(rowBg)
                                                .clickable { onTrackSelected(overallIndex) }
                                                .padding(start = 24.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${track.artist} - ${track.title}",
                                                color = textColor,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = track.formattedDuration,
                                                color = if (isSelected) Color.White else AmDroidLcdDimCyan,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MODO LISTA COMPLETA POR TÍTULO
                    PlaylistSortMode.DEFAULT -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(tracks) { index, track ->
                                val isSelected = index == currentTrackIndex
                                val rowBg = if (isSelected) AmDroidPlaylistSelected else Color.Transparent
                                val textColor = if (isSelected) Color.White else Color(0xFFE2E8F0)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .clickable { onTrackSelected(index) }
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${index + 1}. ${track.artist} - ${track.title}",
                                            color = textColor,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "📁 ${track.folderName} • 💿 ${track.album}",
                                            color = if (isSelected) Color(0xFFE0F2FE) else AmDroidPlaylistTextMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = track.formattedDuration,
                                        color = if (isSelected) Color.White else AmDroidLcdDimCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Barra inferior de la lista
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RetroButton(
                    text = "+ AGREGAR",
                    onClick = onAddAudioClick,
                    width = 85.dp,
                    height = 28.dp
                )
                RetroButton(
                    text = "LIMPIAR",
                    onClick = onClearPlaylist,
                    width = 75.dp,
                    height = 28.dp
                )
            }

            // Resumen de la lista
            val totalSec = tracks.sumOf { it.durationMs } / 1000
            val min = totalSec / 60
            val sec = totalSec % 60
            Text(
                text = "${tracks.size} TEMAS [${String.format("%02d:%02d", min, sec)}]",
                color = AmDroidCyanGlow,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SortPill(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) AmDroidBlueElectric else AmDroidButtonFace
    val borderCol = if (isSelected) AmDroidCyanBright else Color(0xFF1E293B)
    val textCol = if (isSelected) Color.White else AmDroidTextMuted

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(2.dp))
            .border(1.dp, borderCol, RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$icon $label",
            color = textCol,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
