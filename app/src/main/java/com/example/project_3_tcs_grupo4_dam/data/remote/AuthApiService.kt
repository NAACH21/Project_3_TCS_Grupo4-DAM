package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthDtos.LoginRequest): Response<AuthDtos.AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthDtos.RegisterRequest): Response<AuthDtos.AuthResponse>
}

