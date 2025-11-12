package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import retrofit2.http.GET

interface ApiService {
    @GET("api/Colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorReadDto>
}