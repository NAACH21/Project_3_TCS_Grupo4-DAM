package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ColaboradorApiService {

    @GET("api/colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorListDto>

    // CORRECCIÓN: Devolvemos Response<ColaboradorReadDto> DIRECTO
    // El backend devuelve el objeto JSON directo, no envuelto en "success/data"
    @GET("api/colaboradores/{id}")
    suspend fun getColaboradorById(@Path("id") id: String): Response<ColaboradorReadDto>

    @POST("api/colaboradores")
    suspend fun createColaborador(@Body body: ColaboradorCreateDto): Response<ColaboradorReadDto>

    @PUT("api/colaboradores/{id}")
    suspend fun updateColaborador(@Path("id") id: String, @Body body: Any): Response<ColaboradorReadDto>

    @DELETE("api/colaboradores/{id}")
    suspend fun deleteColaborador(@Path("id") id: String): Response<Unit>
}
