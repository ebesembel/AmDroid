package com.amdroid.player.audio

import android.media.audiofx.Equalizer

data class EqPreset(
    val name: String,
    val bandGains: List<Float> // -15.0f to +15.0f dB for 10 bands
)

class EqualizerManager {

    private var equalizer: Equalizer? = null
    var isEnabled: Boolean = true
        private set

    var preampGainDb: Float = 2.0f // Slight preamp punch
        private set

    // Default V-Shape Curve: Punchy Bass & Crisp Treble
    val bandLevels = floatArrayOf(
        6.5f,  // 60Hz: Sub-bass profundo
        5.0f,  // 170Hz: Golpe de bajo
        2.5f,  // 310Hz: Bajos medios
        0.0f,  // 600Hz: Medios bajos
        -0.5f, // 1kHz: Medios
        0.5f,  // 3kHz: Medios altos
        3.0f,  // 6kHz: Presencia de agudos
        5.5f,  // 12kHz: Brillo
        7.0f,  // 14kHz: Nitidez
        7.5f   // 16kHz: Aire y agudos cristalinos
    )

    val classicFrequencies = listOf(
        "60Hz", "170Hz", "310Hz", "600Hz", "1kHz",
        "3kHz", "6kHz", "12kHz", "14kHz", "16kHz"
    )

    val presets = listOf(
        EqPreset(
            "Bajos & Agudos Pro (Recomendado)",
            listOf(6.5f, 5.0f, 2.5f, 0.0f, -0.5f, 0.5f, 3.0f, 5.5f, 7.0f, 7.5f)
        ),
        EqPreset(
            "Bajos Pesados (Bass Boost Extremo)",
            listOf(9.0f, 8.0f, 6.0f, 3.0f, 0.0f, -1.0f, -2.0f, 0.0f, 2.0f, 3.0f)
        ),
        EqPreset(
            "Agudos Cristalinos (Treble Boost)",
            listOf(0.0f, 0.0f, 0.0f, 0.5f, 1.5f, 3.5f, 5.5f, 7.5f, 8.5f, 9.0f)
        ),
        EqPreset(
            "Rock & Metal Intenso",
            listOf(6.0f, 4.5f, -1.0f, -2.0f, -0.5f, 2.5f, 4.5f, 6.0f, 7.0f, 7.0f)
        ),
        EqPreset(
            "Reggaeton / Urbano / Dembow",
            listOf(8.0f, 7.0f, 4.0f, 1.0f, 0.0f, 2.0f, 3.5f, 5.0f, 6.5f, 6.0f)
        ),
        EqPreset(
            "Salsa / Caribe / Tropical",
            listOf(5.0f, 3.5f, 1.0f, 0.0f, 1.5f, 3.0f, 4.5f, 5.5f, 6.0f, 6.0f)
        ),
        EqPreset(
            "Pop Nítido & Voces",
            listOf(2.0f, 3.0f, 4.0f, 4.5f, 3.0f, 1.0f, 2.0f, 4.0f, 5.0f, 5.5f)
        ),
        EqPreset(
            "Electrónica / Club / Techno",
            listOf(7.5f, 6.0f, 2.5f, 0.0f, -1.5f, 2.0f, 4.5f, 6.0f, 7.0f, 7.0f)
        ),
        EqPreset(
            "Plano (Original Sin Efecto)",
            listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        )
    )

    fun attachToAudioSession(audioSessionId: Int) {
        release()
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                setEnabled(isEnabled)
            }
            applyAllBands()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        try {
            equalizer?.setEnabled(enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setPreamp(db: Float) {
        preampGainDb = db.coerceIn(-15.0f, 15.0f)
        applyAllBands()
    }

    fun setBandLevel(bandIndex: Int, levelDb: Float) {
        if (bandIndex in bandLevels.indices) {
            bandLevels[bandIndex] = levelDb.coerceIn(-15.0f, 15.0f)
            applyBand(bandIndex)
        }
    }

    fun adjustBand(bandIndex: Int, deltaDb: Float) {
        if (bandIndex in bandLevels.indices) {
            setBandLevel(bandIndex, bandLevels[bandIndex] + deltaDb)
        }
    }

    fun adjustPreamp(deltaDb: Float) {
        setPreamp(preampGainDb + deltaDb)
    }

    fun applyPreset(preset: EqPreset) {
        for (i in bandLevels.indices) {
            if (i < preset.bandGains.size) {
                bandLevels[i] = preset.bandGains[i]
            }
        }
        applyAllBands()
    }

    private fun applyAllBands() {
        val eq = equalizer ?: return
        val numHardwareBands = eq.numberOfBands.toInt()
        val minRange = eq.bandLevelRange[0] // in mB (millibels, e.g. -1500)
        val maxRange = eq.bandLevelRange[1] // in mB (e.g. +1500)

        for (hBand in 0 until numHardwareBands) {
            val centerFreq = eq.getCenterFreq(hBand.toShort()) / 1000 // Hz
            val virtualIndex = mapFreqToVirtualIndex(centerFreq)
            val desiredDb = (bandLevels[virtualIndex] + preampGainDb).coerceIn(-15f, 15f)
            val millibels = (desiredDb * 100).toInt().coerceIn(minRange.toInt(), maxRange.toInt()).toShort()
            try {
                eq.setBandLevel(hBand.toShort(), millibels)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun applyBand(bandIndex: Int) {
        applyAllBands()
    }

    private fun mapFreqToVirtualIndex(centerFreqHz: Int): Int {
        val targets = intArrayOf(60, 170, 310, 600, 1000, 3000, 6000, 12000, 14000, 16000)
        var closestIdx = 0
        var minDiff = Int.MAX_VALUE
        for (i in targets.indices) {
            val diff = kotlin.math.abs(targets[i] - centerFreqHz)
            if (diff < minDiff) {
                minDiff = diff
                closestIdx = i
            }
        }
        return closestIdx
    }

    fun release() {
        try {
            equalizer?.setEnabled(false)
            equalizer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        equalizer = null
    }
}
