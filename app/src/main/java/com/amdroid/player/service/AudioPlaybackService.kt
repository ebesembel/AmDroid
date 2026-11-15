package com.amdroid.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.amdroid.player.MainActivity
import com.amdroid.player.R
import com.amdroid.player.audio.EqualizerManager
import com.amdroid.player.audio.VisualizerManager
import com.amdroid.player.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlaybackService : Service() {

    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

    val equalizerManager = EqualizerManager()
    val visualizerManager = VisualizerManager()

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playlist = MutableStateFlow<List<Track>>(emptyList())
    val playlist: StateFlow<List<Track>> = _playlist.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(-1)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    private var masterVolume = 1.0f
    private var panBalance = 0.0f // -1.0 (Left) to +1.0 (Right)

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        try {
            mediaSession = MediaSessionCompat(this, "AmDroidSession").apply {
                isActive = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        createNotificationChannel()
        startTicker()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PREV -> previous()
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_NEXT -> next()
            ACTION_CLOSE -> stopAndExit()
        }
        return START_NOT_STICKY
    }

    private fun startTicker() {
        progressJob = serviceScope.launch {
            while (true) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _currentPositionMs.value = it.currentPosition.toLong()
                    }
                }
                visualizerManager.simulateFrame(_isPlaying.value)
                delay(40) // ~25 fps update for visualizer and counter
            }
        }
    }

    fun setPlaylist(tracks: List<Track>, startIdx: Int = 0) {
        _playlist.value = tracks
        if (tracks.isNotEmpty()) {
            val safeIdx = startIdx.coerceIn(tracks.indices)
            _currentTrackIndex.value = safeIdx
            playTrack(safeIdx)
        }
    }

    fun playTrack(index: Int) {
        val list = _playlist.value
        if (index !in list.indices) return
        _currentTrackIndex.value = index
        val track = list[index]

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(applicationContext, track.contentUri)
                prepare()
                start()
                _isPlaying.value = true
                applyVolumeAndBalance()

                equalizerManager.attachToAudioSession(audioSessionId)
                visualizerManager.attachToAudioSession(audioSessionId)

                setOnCompletionListener {
                    handleTrackCompletion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
        updateForegroundNotification(track)
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
            val currentIdx = _currentTrackIndex.value
            if (currentIdx in _playlist.value.indices) {
                updateForegroundNotification(_playlist.value[currentIdx])
            }
        } ?: run {
            if (_playlist.value.isNotEmpty()) {
                playTrack(if (_currentTrackIndex.value >= 0) _currentTrackIndex.value else 0)
            }
        }
    }

    fun stop() {
        mediaPlayer?.let {
            it.stop()
            try {
                it.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            it.seekTo(0)
            _isPlaying.value = false
            _currentPositionMs.value = 0L
        }
    }

    fun next() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val nextIdx = if (_isShuffle.value) {
            (list.indices).random()
        } else {
            (_currentTrackIndex.value + 1) % list.size
        }
        playTrack(nextIdx)
    }

    fun previous() {
        val list = _playlist.value
        if (list.isEmpty()) return
        mediaPlayer?.let {
            if (it.currentPosition > 3000) {
                it.seekTo(0)
                return
            }
        }
        val prevIdx = if (_currentTrackIndex.value - 1 < 0) list.size - 1 else _currentTrackIndex.value - 1
        playTrack(prevIdx)
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let {
            it.seekTo(positionMs.toInt())
            _currentPositionMs.value = positionMs
        }
    }

    fun setVolume(vol: Float) {
        masterVolume = vol.coerceIn(0.0f, 1.0f)
        applyVolumeAndBalance()
    }

    fun setBalance(bal: Float) {
        panBalance = bal.coerceIn(-1.0f, 1.0f)
        applyVolumeAndBalance()
    }

    private fun applyVolumeAndBalance() {
        val left = if (panBalance <= 0) masterVolume else masterVolume * (1.0f - panBalance)
        val right = if (panBalance >= 0) masterVolume else masterVolume * (1.0f + panBalance)
        try {
            mediaPlayer?.setVolume(left, right)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
    }

    fun stopAndExit() {
        stop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.cancel(NOTIFICATION_ID)
        onExitRequested?.invoke()
        stopSelf()
    }

    private fun handleTrackCompletion() {
        if (_isRepeat.value) {
            playTrack(_currentTrackIndex.value)
        } else {
            next()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_description)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateForegroundNotification(track: Track) {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getService(
            this, 1, Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_PREV },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseIntent = PendingIntent.getService(
            this, 2, Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_PLAY_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = PendingIntent.getService(
            this, 3, Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_NEXT },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val closeIntent = PendingIntent.getService(
            this, 4, Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_CLOSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (_isPlaying.value) R.drawable.ic_notif_pause else R.drawable.ic_notif_play
        val playPauseTitle = if (_isPlaying.value) "Pausa" else "Reproducir"

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
        mediaSession?.let {
            mediaStyle.setMediaSession(it.sessionToken)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(track.title)
            .setContentText("${track.artist} • AmDroid")
            .setSubText("AmDroid")
            .setContentIntent(pendingIntent)
            .setOngoing(_isPlaying.value)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_notif_prev, "Anterior", prevIntent)
            .addAction(playPauseIcon, playPauseTitle, playPauseIntent)
            .addAction(R.drawable.ic_notif_next, "Siguiente", nextIntent)
            .addAction(R.drawable.ic_notif_close, "Cerrar", closeIntent)
            .setStyle(mediaStyle)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        progressJob?.cancel()
        equalizerManager.release()
        visualizerManager.release()
        mediaSession?.release()
        mediaSession = null
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "amdroid_playback_channel"
        const val NOTIFICATION_ID = 1997

        const val ACTION_PREV = "com.amdroid.player.ACTION_PREV"
        const val ACTION_PLAY_PAUSE = "com.amdroid.player.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.amdroid.player.ACTION_NEXT"
        const val ACTION_CLOSE = "com.amdroid.player.ACTION_CLOSE"

        var onExitRequested: (() -> Unit)? = null
    }
}
