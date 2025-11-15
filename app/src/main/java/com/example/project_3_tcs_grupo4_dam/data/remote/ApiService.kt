package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorReadDto>

    @GET("api/colaboradores/{id}")
    suspend fun getColaboradorById(@Path("id") id: String): ColaboradorReadDto
}
