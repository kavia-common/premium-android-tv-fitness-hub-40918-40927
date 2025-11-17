package com.example.tv_app_frontend.data.local

import android.content.Context
import com.example.tv_app_frontend.data.remote.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException

/**
 * PUBLIC_INTERFACE
 * LocalDataSource serves static/mock content from assets for offline operation.
 *
 * It reads app/src/main/assets/mock/content.json once and keeps an in-memory cache.
 * The JSON schema aligns with the remote ApiService data classes to minimize changes.
 */
object LocalDataSource {

    private lateinit var appContext: Context
    private val gson = Gson()
    @Volatile private var cache: LocalContent? = null

    /**
     * PUBLIC_INTERFACE
     * Initialize the data source with application context (call in Application.onCreate).
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * PUBLIC_INTERFACE
     * Fetch home sections (Continue, Favorites, Recent, Categories).
     */
    suspend fun getHome(): HomeResponse {
        val content = getOrLoad()
        return content.home
    }

    /**
     * PUBLIC_INTERFACE
     * Fetch workouts with optional filters (category, level, duration range in minutes).
     */
    suspend fun getWorkouts(
        category: String? = null,
        level: String? = null,
        durationMin: Int? = null,
        durationMax: Int? = null
    ): WorkoutsResponse {
        val content = getOrLoad()
        val items = content.workouts.filter { w ->
            val catOk = category?.let { it.equals(w.category ?: "", ignoreCase = true) } ?: true
            val lvlOk = level?.let { it.equals(w.level ?: "", ignoreCase = true) } ?: true
            val minOk = durationMin?.let { (w.durationSec / 60) >= it } ?: true
            val maxOk = durationMax?.let { (w.durationSec / 60) <= it } ?: true
            catOk && lvlOk && minOk && maxOk
        }
        return WorkoutsResponse(items)
    }

    /**
     * PUBLIC_INTERFACE
     * Fetch workout detail by id, falling back to a synthesized detail if none provided.
     */
    suspend fun getWorkout(id: String): WorkoutDetail {
        val content = getOrLoad()
        val explicit = content.details[id]
        if (explicit != null) return explicit

        // Synthesize a minimal detail from the list when explicit detail is not present
        val fromItem = content.workouts.firstOrNull { it.id == id }
            ?: content.home.favorites.firstOrNull { it.id == id }
            ?: content.home.recent.firstOrNull { it.id == id }
            ?: content.home.continueWatching.firstOrNull { it.id == id }

        if (fromItem != null) {
            return WorkoutDetail(
                id = fromItem.id,
                title = fromItem.title,
                description = "A guided ${fromItem.level ?: ""} workout in the ${fromItem.category ?: "General"} category.",
                // Demo HLS stream (static, not fetched from backend)
                videoUrl = DEMO_VIDEO_URL,
                audioDescriptionUrl = null,
                ttsEnabled = false,
                durationSec = fromItem.durationSec,
                level = fromItem.level,
                category = fromItem.category,
                equipment = emptyList()
            )
        }

        // Absolute fallback to a demo detail
        return WorkoutDetail(
            id = id,
            title = "Demo Workout",
            description = "Demo workout detail not found in local content; showing placeholder.",
            videoUrl = DEMO_VIDEO_URL,
            audioDescriptionUrl = null,
            ttsEnabled = false,
            durationSec = 15 * 60,
            level = "Beginner",
            category = "General",
            equipment = emptyList()
        )
    }

    private fun getOrLoad(): LocalContent {
        val existing = cache
        if (existing != null) return existing
        synchronized(this) {
            val again = cache
            if (again != null) return again
            val loaded = loadFromAssets()
            cache = loaded
            return loaded
        }
    }

    private fun loadFromAssets(): LocalContent {
        ensureInitialized()
        val am = appContext.assets
        try {
            am.open(MOCK_CONTENT_PATH).use { input ->
                BufferedReader(InputStreamReader(input)).use { br ->
                    val json = br.readText()
                    val content = gson.fromJson(json, LocalContent::class.java)
                    return content ?: throw IllegalStateException("Parsed content is null")
                }
            }
        } catch (ex: JsonSyntaxException) {
            throw IllegalStateException("Invalid JSON in assets/$MOCK_CONTENT_PATH: ${ex.message}", ex)
        } catch (ex: Exception) {
            // If anything goes wrong, ship a minimal hardcoded fallback
            return fallbackContent()
        }
    }

    private fun ensureInitialized() {
        check(::appContext.isInitialized) {
            "LocalDataSource.initialize(context) must be called in Application.onCreate()"
        }
    }

    private fun fallbackContent(): LocalContent {
        val fallbackWorkouts = listOf(
            WorkoutItem(
                id = "fallback01",
                title = "Quick HIIT Blast",
                thumbnailUrl = null,
                durationSec = 12 * 60,
                level = "Intermediate",
                category = "HIIT"
            ),
            WorkoutItem(
                id = "fallback02",
                title = "Morning Yoga Flow",
                thumbnailUrl = null,
                durationSec = 20 * 60,
                level = "Beginner",
                category = "Yoga"
            ),
        )
        val home = HomeResponse(
            favorites = fallbackWorkouts,
            recent = fallbackWorkouts.reversed(),
            continueWatching = emptyList(),
            categories = listOf(
                CategoryItem(id = "c1", name = "HIIT", heroImageUrl = null),
                CategoryItem(id = "c2", name = "Yoga", heroImageUrl = null)
            )
        )
        val details = mapOf(
            "fallback01" to WorkoutDetail(
                id = "fallback01",
                title = "Quick HIIT Blast",
                description = "A short, intense HIIT session.",
                videoUrl = DEMO_VIDEO_URL,
                audioDescriptionUrl = null,
                ttsEnabled = false,
                durationSec = 12 * 60,
                level = "Intermediate",
                category = "HIIT",
                equipment = emptyList()
            ),
            "fallback02" to WorkoutDetail(
                id = "fallback02",
                title = "Morning Yoga Flow",
                description = "Gentle yoga to start the day.",
                videoUrl = DEMO_VIDEO_URL,
                audioDescriptionUrl = null,
                ttsEnabled = false,
                durationSec = 20 * 60,
                level = "Beginner",
                category = "Yoga",
                equipment = emptyList()
            )
        )
        return LocalContent(home = home, workouts = fallbackWorkouts, details = details)
    }

    private const val MOCK_CONTENT_PATH = "mock/content.json"
    private const val DEMO_VIDEO_URL = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
}

/**
 * Local content schema mirroring remote models for minimal friction.
 */
data class LocalContent(
    val home: HomeResponse,
    val workouts: List<WorkoutItem>,
    val details: Map<String, WorkoutDetail> = emptyMap()
)
