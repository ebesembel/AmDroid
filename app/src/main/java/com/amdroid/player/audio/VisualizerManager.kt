package com.amdroid.player.audio

import android.media.audiofx.Visualizer
import kotlin.math.abs
import kotlin.math.hypot

enum class VisualizerMode {
    SPECTRUM,
    OSCILLOSCOPE,
    OFF
}

class VisualizerManager {

    private var visualizer: Visualizer? = null
    var mode: VisualizerMode = VisualizerMode.SPECTRUM

    // 16 bars for the spectrum analyzer
    val barValues = FloatArray(16) { 0f }
    val peakValues = FloatArray(16) { 0f }

    // Waveform points for oscilloscope (64 points)
    val wavePoints = FloatArray(64) { 0f }

    fun attachToAudioSession(audioSessionId: Int) {
        release()
        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[0].coerceAtLeast(128)
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        v: Visualizer?,
                        waveform: ByteArray?,
                        samplingRate: Int
                    ) {
                        waveform?.let { processWaveform(it) }
                    }

                    override fun onFftDataCapture(
                        v: Visualizer?,
                        fft: ByteArray?,
                        samplingRate: Int
                    ) {
                        fft?.let { processFft(it) }
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true)
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If failed (e.g. permission or hardware limitation), we fallback to simulation
        }
    }

    private fun processFft(fft: ByteArray) {
        if (mode != VisualizerMode.SPECTRUM) return
        val n = fft.size
        if (n < 32) return

        val bands = barValues.size
        val step = (n / 2) / bands

        for (i in 0 until bands) {
            var sum = 0f
            for (j in 0 until step) {
                val index = 2 + (i * step + j) * 2
                if (index + 1 < n) {
                    val r = fft[index].toFloat()
                    val im = fft[index + 1].toFloat()
                    sum += hypot(r, im)
                }
            }
            val avg = (sum / step) / 128f
            val normalized = avg.coerceIn(0f, 1f)

            // Smooth bar transition
            barValues[i] = barValues[i] * 0.4f + normalized * 0.6f

            // Gravity peak drop
            if (barValues[i] >= peakValues[i]) {
                peakValues[i] = barValues[i]
            } else {
                peakValues[i] = (peakValues[i] - 0.04f).coerceAtLeast(0f)
            }
        }
    }

    private fun processWaveform(waveform: ByteArray) {
        if (mode != VisualizerMode.OSCILLOSCOPE) return
        val step = waveform.size / wavePoints.size
        for (i in wavePoints.indices) {
            val idx = (i * step).coerceIn(waveform.indices)
            // waveform contains unsigned 8-bit values from 0 to 255 (128 is center)
            val v = (waveform[idx].toInt() and 0xFF) - 128
            wavePoints[i] = v / 128f
        }
    }

    // Called when simulating playback or if visualizer hardware is unavailable
    fun simulateFrame(isPlaying: Boolean) {
        if (!isPlaying) {
            for (i in barValues.indices) {
                barValues[i] = (barValues[i] * 0.8f).coerceAtLeast(0f)
                peakValues[i] = (peakValues[i] - 0.03f).coerceAtLeast(0f)
            }
            for (i in wavePoints.indices) {
                wavePoints[i] = wavePoints[i] * 0.8f
            }
            return
        }

        if (visualizer == null || !visualizer!!.enabled) {
            // Generates organic music-like dancing bars
            for (i in barValues.indices) {
                val target = (0.2f + Math.random().toFloat() * 0.8f) * (1.0f - (i * 0.03f))
                barValues[i] = barValues[i] * 0.5f + target * 0.5f
                if (barValues[i] > peakValues[i]) {
                    peakValues[i] = barValues[i]
                } else {
                    peakValues[i] = (peakValues[i] - 0.04f).coerceAtLeast(0f)
                }
            }
            for (i in wavePoints.indices) {
                val t = System.currentTimeMillis() / 100.0
                wavePoints[i] = (Math.sin(t + i * 0.2) * 0.6).toFloat()
            }
        }
    }

    fun release() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        visualizer = null
    }
}
