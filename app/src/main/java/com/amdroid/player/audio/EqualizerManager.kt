package com.amdroid.player.audio

import android.media.audiofx.Equalizer

data class EqPreset(
    val name: String,
    val bandGains: List<Float> // -12.0f to +12.0f dB for 10 bands
)

class EqualizerManager {

    private var equalizer: Equalizer? = null
    var isEnabled: Boolean = true
        private set

    var preampGainDb: Float = 0.0f
        private set

    // 10 virtual Winamp bands in dB (-12f to +12f)
    val bandLevels = FloatArray(10) { 0f }

    val classicFrequencies = listOf(
        "60Hz", "170Hz", "310Hz", "600Hz", "1kHz",
        "3kHz", "6kHz", "12kHz", "14kHz", "16kHz"
    )

    val presets = listOf(
        EqPreset("Flat", List(10) { 0f }),
        EqPreset("Rock", listOf(4.5f, 3.0f, -1.5f, -2.5f, -0.5f, 2.5f, 4.0f, 5.5f, 6.0f, 6.0f)),
        EqPreset("Full Bass", listOf(7.0f, 6.5f, 5.0f, 2.0f, -1.0f, -3.0f, -4.0f, -5.0f, -5.5f, -6.0f)),
        EqPreset("Pop", listOf(-1.5f, 1.0f, 3.5f, 4.0f, 3.0f, -1.0f, -2.0f, -2.0f, -1.5f, -1.5f)),
        EqPreset("Techno", listOf(5.0f, 4.0f, 2.0f, 0.0f, -2.0f, 2.0f, 4.5f, 5.0f, 5.5f, 5.0f)),
        EqPreset("Classical", listOf(4.0f, 3.0f, 2.5f, 2.0f, -1.5f, -1.5f, 0.0f, 2.0f, 3.0f, 3.5f)),
        EqPreset("Reggae", listOf(0.0f, 0.0f, -0.5f, -4.0f, 0.0f, 4.5f, 4.5f, 2.5f, 0.0f, 0.0f)),
        EqPreset("Ska", listOf(-2.0f, -3.5f, -3.0f, -1.0f, 2.0f, 4.0f, 6.0f, 6.5f, 7.0f, 6.0f)),
        EqPreset("Party", listOf(5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 5.0f))
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
        preampGainDb = db.coerceIn(-12.0f, 12.0f)
        applyAllBands()
    }

    fun setBandLevel(bandIndex: Int, levelDb: Float) {
        if (bandIndex in bandLevels.indices) {
            bandLevels[bandIndex] = levelDb.coerceIn(-12.0f, 12.0f)
            applyBand(bandIndex)
        }
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
            // Map hardware band frequency to closest virtual band
            val centerFreq = eq.getCenterFreq(hBand.toShort()) / 1000 // Hz
            val virtualIndex = mapFreqToVirtualIndex(centerFreq)
            val desiredDb = (bandLevels[virtualIndex] + preampGainDb).coerceIn(-12f, 12f)
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
            equalizer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        equalizer = null
    }
}
