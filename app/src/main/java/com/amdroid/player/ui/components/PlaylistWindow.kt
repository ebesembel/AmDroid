package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    RetroWindowBox(
        title = "AmDroid - Lista de Reproducción",
        modifier = modifier
    ) {
        // Barra de Ordenamiento (Carpetas, Álbum, Nombre, Mezclar)
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
                // Filtro por Nombre
                SortPill(
                    label = "TITULO",
                    icon = "📋",
                    isSelected = currentSortMode == PlaylistSortMode.DEFAULT,
                    onClick = { onSortModeChanged(PlaylistSortMode.DEFAULT) }
                )

                // Filtro por Carpeta
                SortPill(
                    label = "CARPETAS",
                    icon = "📁",
                    isSelected = currentSortMode == PlaylistSortMode.FOLDER,
                    onClick = { onSortModeChanged(PlaylistSortMode.FOLDER) }
                )

                // Filtro por Álbum
                SortPill(
                    label = "ÁLBUM",
                    icon = "💿",
                    isSelected = currentSortMode == PlaylistSortMode.ALBUM,
                    onClick = { onSortModeChanged(PlaylistSortMode.ALBUM) }
                )
            }

            // Botón rápido para Mezclar el orden aleatoriamente
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

        // Ventana de canciones
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    var lastHeader = ""

                    itemsIndexed(tracks) { index, track ->
                        val isSelected = index == currentTrackIndex
                        val rowBg = if (isSelected) AmDroidPlaylistSelected else Color.Transparent
                        val textColor = if (isSelected) Color.White else Color(0xFFE2E8F0)

                        // Si estamos ordenando por carpeta o álbum, mostramos cabecera divisoria
                        val currentHeader = when (currentSortMode) {
                            PlaylistSortMode.FOLDER -> "📁 CARPETA: ${track.folderName}"
                            PlaylistSortMode.ALBUM -> "💿 ÁLBUM: ${track.album}"
                            PlaylistSortMode.DEFAULT -> ""
                        }

                        if (currentHeader.isNotEmpty() && currentHeader != lastHeader) {
                            lastHeader = currentHeader
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF131A26))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = currentHeader.uppercase(),
                                    color = AmDroidCyanGlow,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

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

                                if (currentSortMode == PlaylistSortMode.DEFAULT && track.album.isNotBlank()) {
                                    Text(
                                        text = "${track.album} • ${track.folderName}",
                                        color = if (isSelected) Color(0xFFE0F2FE) else AmDroidPlaylistTextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
