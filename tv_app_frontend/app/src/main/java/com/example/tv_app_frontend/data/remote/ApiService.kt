package com.example.tv_app_frontend.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// PUBLIC_INTERFACE
interface ApiService {
    /** Fetch home sections such as categories, favorites, recent, continue. */
    @GET("tv/home")
    suspend fun getHome(): HomeResponse

    /** Fetch workouts with optional filters. */
    @GET("workouts")
    suspend fun getWorkouts(
        @Query("category") category: String? = null,
        @Query("level") level: String? = null,
        @Query("durationMin") durationMin: Int? = null,
        @Query("durationMax") durationMax: Int? = null
    ): WorkoutsResponse

    /** Fetch workout details. */
    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") id: String): WorkoutDetailResponse

    /** Fetch recommendations for current user. */
    @GET("tv/recommendations")
    suspend fun getRecommendations(): WorkoutsResponse
}

data class HomeResponse(
    val favorites: List<WorkoutItem>,
    val recent: List<WorkoutItem>,
    val continueWatching: List<WorkoutItem>,
    val categories: List<CategoryItem>
)

data class WorkoutsResponse(val items: List<WorkoutItem>)
data class WorkoutDetailResponse(val item: WorkoutDetail)

data class WorkoutItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val durationSec: Int,
    val level: String?,
    val category: String?
)

data class WorkoutDetail(
    val id: String,
    val title: String,
    val description: String?,
    val videoUrl: String,
    val audioDescriptionUrl: String?,
    val ttsEnabled: Boolean?,
    val durationSec: Int,
    val level: String?,
    val category: String?,
    val equipment: List<String> = emptyList()
)

data class CategoryItem(
    val id: String,
    val name: String,
    val heroImageUrl: String?
)
