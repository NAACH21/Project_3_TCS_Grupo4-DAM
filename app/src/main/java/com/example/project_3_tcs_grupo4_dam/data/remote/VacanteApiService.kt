package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VacanteApiService {
    @GET("api/vacantes") // Asegúrate de que este sea el endpoint correcto
    suspend fun getVacantes(): List<Vacante>

    @POST("api/vacantes")
    suspend fun createVacante(@Body vacante: VacanteCreateDto): Vacante
}
