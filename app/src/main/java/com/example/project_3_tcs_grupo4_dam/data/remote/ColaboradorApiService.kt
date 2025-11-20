package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto // Importar el DTO de lista
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ColaboradorApiService {

    // CORRECCIÓN: Usar ColaboradorListDto para la lista para evitar errores de parseo de skills
    @GET("api/colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorListDto>

    @GET("api/colaboradores/{id}")
    suspend fun getColaboradorById(@Path("id") id: String): ColaboradorReadDto

    @POST("api/colaboradores")
    suspend fun createColaborador(@Body body: ColaboradorCreateDto): ColaboradorReadDto

    @PUT("api/colaboradores/{id}")
    suspend fun updateColaborador(@Path("id") id: String, @Body body: ColaboradorCreateDto): ColaboradorReadDto

    @DELETE("api/colaboradores/{id}")
    suspend fun deleteColaborador(@Path("id") id: String)
}
