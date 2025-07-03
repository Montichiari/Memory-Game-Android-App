package com.example.group3ca.network

import retrofit2.Response
import retrofit2.http.*

interface LeaderboardApi {

    @POST("api/leaderboard")
    suspend fun postScore(@Body dto: ScoreDto): Response<Unit>

    @GET("api/leaderboard/top/{count}")
    suspend fun topScores(@Path("count") count: Int = 5): Response<List<ScoreDto>>
}