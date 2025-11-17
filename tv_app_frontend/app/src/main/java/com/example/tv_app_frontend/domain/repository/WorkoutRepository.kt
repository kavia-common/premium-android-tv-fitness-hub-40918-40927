package com.example.tv_app_frontend.domain.repository

import com.example.tv_app_frontend.data.remote.*

/**
 * Repository for workout list and details.
 */
class WorkoutRepository {
    private val api = ApiClient.create(ApiService::class.java)

    suspend fun fetchWorkouts(
        category: String? = null,
        level: String? = null,
        durationMin: Int? = null,
        durationMax: Int? = null
    ): WorkoutsResponse = api.getWorkouts(category, level, durationMin, durationMax)

    suspend fun fetchWorkoutDetail(id: String): WorkoutDetail =
        api.getWorkout(id).item
}
