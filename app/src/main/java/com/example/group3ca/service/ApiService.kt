package com.example.group3ca.service

import com.example.group3ca.dto.GameRequestDto
import com.example.group3ca.dto.LeaderboardEntry
import com.example.group3ca.dto.LoginRequestDto
import com.example.group3ca.dto.LoginResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @POST("api/User/login")
    fun login(@Body request: LoginRequestDto): Call<LoginResponseDto>

    @POST("api/Game/savegame")
    fun createGame(@Body request: GameRequestDto): Call<ResponseBody>

    @GET("api/Game/leaderboard")
    fun getLeaderboard(): Call<List<LeaderboardEntry>>
}