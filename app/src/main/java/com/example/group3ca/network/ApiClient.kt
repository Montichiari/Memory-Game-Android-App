package com.example.group3ca.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    /** 调试：模拟器访问本机用 10.0.2.2 */
    private const val BASE_URL = "http://10.0.2.2:5180/"

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    val api: LeaderboardApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LeaderboardApi::class.java)
    }
}