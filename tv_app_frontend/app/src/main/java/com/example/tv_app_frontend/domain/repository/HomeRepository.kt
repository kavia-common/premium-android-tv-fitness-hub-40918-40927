package com.example.tv_app_frontend.domain.repository

import com.example.tv_app_frontend.data.local.LocalDataSource
import com.example.tv_app_frontend.data.remote.HomeResponse
import com.example.tv_app_frontend.data.remote.WorkoutsResponse

/**
 * Repository to access home data and recommendations (local/mock only).
 */
class HomeRepository {

    suspend fun fetchHome(): HomeResponse = LocalDataSource.getHome()

    suspend fun fetchRecommendations(): WorkoutsResponse =
        LocalDataSource.getWorkouts(category = null, level = null, durationMin = null, durationMax = null)
}
