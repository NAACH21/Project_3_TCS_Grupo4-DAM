package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import retrofit2.http.*

interface VacanteApiService {
    @GET("api/vacantes")
    suspend fun getVacantes(): List<VacanteResponse>

    /**
     * Obtiene vacantes con filtro opcional por estado activo
     */
    @GET("api/vacantes")
    suspend fun getVacantes(@Query("activa") activa: Boolean?): List<VacanteResponse>

    @GET("api/Vacantes/{id}")
    suspend fun getVacanteById(@Path("id") id: String): VacanteResponse

    @POST("api/vacantes")
    suspend fun createVacante(@Body vacante: VacanteCreateDto): Vacante

    @PUT("api/Vacantes/{id}")
    suspend fun updateVacante(@Path("id") id: String, @Body body: VacanteResponse): VacanteResponse

    @DELETE("api/Vacantes/{id}")
    suspend fun deleteVacante(@Path("id") id: String)
}