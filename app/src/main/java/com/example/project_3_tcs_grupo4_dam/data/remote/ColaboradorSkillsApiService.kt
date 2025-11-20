package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.CreateColaboradorSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.UpdateColaboradorSkillDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ColaboradorSkillsApiService {

    @GET("api/colaboradores/{id}/skills")
    suspend fun getSkillsByColaborador(@Path("id") colaboradorId: String): List<ColaboradorSkillDto>

    @GET("api/colaboradores/{id}/skills/{skillId}")
    suspend fun getSkillByColaboradorAndSkillId(
        @Path("id") colaboradorId: String,
        @Path("skillId") skillId: String
    ): ColaboradorSkillDto

    @POST("api/colaboradores/{id}/skills")
    suspend fun addSkillToColaborador(
        @Path("id") colaboradorId: String,
        @Body body: CreateColaboradorSkillDto
    ): ColaboradorSkillDto

    @PUT("api/colaboradores/{id}/skills/{skillId}")
    suspend fun updateColaboradorSkill(
        @Path("id") colaboradorId: String,
        @Path("skillId") skillId: String,
        @Body body: UpdateColaboradorSkillDto
    ): ColaboradorSkillDto

    @DELETE("api/colaboradores/{id}/skills/{skillId}")
    suspend fun deleteColaboradorSkill(
        @Path("id") colaboradorId: String,
        @Path("skillId") skillId: String
    )
}
