package com.example.tv_app_frontend.recommendations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Placeholder receiver to publish recommendations locally when backend is unavailable.
 */
class RecommendationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // In a real app, build Recommendations via TvProvider or Channel API.
    }
}
