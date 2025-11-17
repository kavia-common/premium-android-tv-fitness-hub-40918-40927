package com.example.tv_app_frontend.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * PUBLIC_INTERFACE
 * ProgressStore caches playback progress for workouts to support Continue Watching.
 */
object ProgressStore {
    private const val PREFS = "progress_prefs"

    // PUBLIC_INTERFACE
    fun save(context: Context, workoutId: String, positionMs: Long) {
        /** Save playback position for workout. */
        prefs(context).edit().putLong(workoutId, positionMs).apply()
    }

    // PUBLIC_INTERFACE
    fun get(context: Context, workoutId: String): Long {
        /** Retrieve playback position for workout. */
        return prefs(context).getLong(workoutId, 0)
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
