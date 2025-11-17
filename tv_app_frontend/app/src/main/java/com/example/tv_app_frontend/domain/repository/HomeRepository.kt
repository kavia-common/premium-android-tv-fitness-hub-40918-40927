package com.example.tv_app_frontend.domain.repository

import com.example.tv_app_frontend.data.remote.ApiClient
import com.example.tv_app_frontend.data.remote.ApiService
import com.example.tv_app_frontend.data.remote.HomeResponse

/**
 * Repository to access home data and recommendations.
 */
class HomeRepository {
    private val api = ApiClient.create(ApiService::class.java)

    suspend fun fetchHome(): HomeResponse = api.getHome()

    suspend fun fetchRecommendations() = api.getRecommendations()
}
