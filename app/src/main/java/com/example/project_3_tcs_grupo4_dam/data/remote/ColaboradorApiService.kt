package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ColaboradorApiService {
    @GET("api/Colaboradores")
    suspend fun getColaboradores(): List<ColaboradorResponse>

    @GET("api/Colaboradores/{id}")
    suspend fun getColaboradorById(@Path("id") id: String): ColaboradorResponse

    @POST("api/Colaboradores")
    suspend fun createColaborador(@Body body: ColaboradorCreateDto): ColaboradorResponse

    @PUT("api/Colaboradores/{id}")
    suspend fun updateColaborador(@Path("id") id: String, @Body body: ColaboradorCreateDto): ColaboradorResponse

    @DELETE("api/Colaboradores/{id}")
    suspend fun deleteColaborador(@Path("id") id: String)
}
