package com.example.tv_app_frontend.player

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Minimal playback service placeholder for future foreground playback notifications on TV.
 */
class PlaybackService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
