package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ColaboradorApiService {

    @GET("api/colaboradores")
    suspend fun getAllColaboradores(): List<ColaboradorReadDto>

    @GET("api/colaboradores/{id}")
    suspend fun getColaboradorById(
        @Path("id") id: String
    ): ColaboradorReadDto

    @POST("api/colaboradores")
    suspend fun createColaborador(
        @Body body: ColaboradorCreateDto
    ): ColaboradorReadDto

    @PUT("api/colaboradores/{id}")
    suspend fun updateColaborador(
        @Path("id") id: String,
        @Body body: ColaboradorUpdateDto
    ): ColaboradorReadDto

    @DELETE("api/colaboradores/{id}")
    suspend fun deleteColaborador(@Path("id") id: String): retrofit2.Response<Unit>

    // WORKAROUND: Agregamos esto aquí para no modificar RetrofitClient
    @GET("api/skills")
    suspend fun getAllSkills(): List<SkillDto>
}
