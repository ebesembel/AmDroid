package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun PlaylistWindow(
    tracks: List<Track>,
    currentTrackIndex: Int,
    onTrackSelected: (Int) -> Unit,
    onAddAudioClick: () -> Unit,
    onClearPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    RetroWindowBox(
        title = "AmDroid - Playlist Editor",
        modifier = modifier
    ) {
        // Track list display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(WinampPlaylistBg, RoundedCornerShape(2.dp))
                .border(1.dp, WinampBorderShadow)
                .padding(2.dp)
        ) {
            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ PLAYLIST EMPTY - TAP '+ ADD' TO SCAN DEVICE ]",
                        color = WinampLcdDimGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(tracks) { index, track ->
                        val isSelected = index == currentTrackIndex
                        val rowBg = if (isSelected) WinampPlaylistSelected else Color.Transparent
                        val textColor = if (isSelected) Color.White else WinampLcdGreen

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .clickable { onTrackSelected(index) }
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}. ${track.artist} - ${track.title}",
                                color = textColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = track.formattedDuration,
                                color = if (isSelected) Color.White else WinampLcdDimGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Playlist bottom toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RetroButton(
                    text = "+ ADD",
                    onClick = onAddAudioClick,
                    width = 60.dp,
                    height = 24.dp
                )
                RetroButton(
                    text = "CLEAR",
                    onClick = onClearPlaylist,
                    width = 56.dp,
                    height = 24.dp
                )
            }

            // Playlist status summary
            val totalSec = tracks.sumOf { it.durationMs } / 1000
            val min = totalSec / 60
            val sec = totalSec % 60
            Text(
                text = "${tracks.size} TRACKS [${String.format("%02d:%02d", min, sec)}]",
                color = WinampLcdGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
