package com.amdroid.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.amdroid.player.audio.AudioScanner
import com.amdroid.player.service.AudioPlaybackService
import com.amdroid.player.ui.components.AmDroidScreen
import com.amdroid.player.ui.theme.AmDroidTheme

class MainActivity : ComponentActivity() {

    private var playbackService: AudioPlaybackService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlaybackService.LocalBinder
            playbackService = binder.getService()
            isBound = true

            // If playlist is empty, trigger initial scan
            if (playbackService?.playlist?.value.isNullOrEmpty()) {
                loadAudioFiles()
            }
            updateUi()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playbackService = null
            isBound = false
            updateUi()
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        loadAudioFiles()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start & Bind playback service
        AudioPlaybackService.onExitRequested = {
            finishAffinity()
        }
        val intent = Intent(this, AudioPlaybackService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        checkAndRequestPermissions()
        updateUi()
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Permission for live spectrum visualizer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            loadAudioFiles()
        }
    }

    private fun loadAudioFiles() {
        val tracks = AudioScanner.scanDeviceAudio(this)
        if (tracks.isNotEmpty()) {
            playbackService?.setPlaylist(tracks, startIdx = 0)
        }
    }

    private fun updateUi() {
        setContent {
            AmDroidTheme {
                AmDroidScreen(
                    service = playbackService,
                    onOpenAudioPicker = {
                        checkAndRequestPermissions()
                        loadAudioFiles()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onDestroy()
    }
}
