package com.amdroid.player.model

import android.net.Uri
import java.io.File

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: Uri,
    val path: String? = null,
    val bitrateKbps: Int = 320,
    val sampleRateKhz: Int = 44
) {
    val folderName: String
        get() {
            if (path.isNullOrBlank()) return "Música Principal"
            return try {
                File(path).parentFile?.name ?: "Música"
            } catch (e: Exception) {
                "Música"
            }
        }

    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
