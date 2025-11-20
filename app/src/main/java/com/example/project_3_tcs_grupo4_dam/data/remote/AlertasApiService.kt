package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import retrofit2.http.GET

interface AlertasApiService {
    /**
     * Obtiene TODAS las alertas.
     * El filtrado por rol/colaborador se hace en el ViewModel (Frontend).
     */
    @GET("api/alertas")
    suspend fun getAllAlertas(): List<AlertaDto>
}
