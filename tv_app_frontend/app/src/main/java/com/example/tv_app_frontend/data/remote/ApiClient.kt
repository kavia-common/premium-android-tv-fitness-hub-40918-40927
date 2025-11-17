package com.example.tv_app_frontend.data.remote

import android.content.Context
import com.example.tv_app_frontend.BuildConfig
import com.example.tv_app_frontend.data.auth.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * PUBLIC_INTERFACE
 * ApiClient provides a singleton Retrofit instance with an AuthInterceptor.
 */
object ApiClient {
    private lateinit var retrofit: Retrofit

    // PUBLIC_INTERFACE
    fun initialize(context: Context) {
        /** Initializes Retrofit client with auth and logging interceptors. */
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val auth = AuthInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // PUBLIC_INTERFACE
    fun <T> create(service: Class<T>): T {
        /** Create a Retrofit API service implementation. */
        return retrofit.create(service)
    }
}

/**
 * Interceptor to add Authorization header when token is present.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val access = TokenStore.getAccessToken()
        val original = chain.request()
        val builder = original.newBuilder()
        if (!access.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $access")
        }
        return chain.proceed(builder.build())
    }
}
