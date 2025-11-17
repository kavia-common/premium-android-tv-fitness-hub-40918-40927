package com.example.tv_app_frontend.domain.repository

import com.example.tv_app_frontend.data.local.LocalDataSource
import com.example.tv_app_frontend.data.remote.WorkoutDetail
import com.example.tv_app_frontend.data.remote.WorkoutsResponse

/**
 * Repository for workout list and details (local/mock only).
 */
class WorkoutRepository {

    suspend fun fetchWorkouts(
        category: String? = null,
        level: String? = null,
        durationMin: Int? = null,
        durationMax: Int? = null
    ): WorkoutsResponse = LocalDataSource.getWorkouts(category, level, durationMin, durationMax)

    suspend fun fetchWorkoutDetail(id: String): WorkoutDetail =
        LocalDataSource.getWorkout(id)
}
