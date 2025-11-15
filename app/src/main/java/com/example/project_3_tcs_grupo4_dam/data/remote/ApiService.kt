package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthDtos.LoginRequest): Response<AuthDtos.AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthDtos.RegisterRequest): Response<AuthDtos.AuthResponse>

    @GET("api/colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorReadDto>
}
