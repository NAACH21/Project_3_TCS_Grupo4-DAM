package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import retrofit2.http.GET

interface AlertasApiService {

    @GET("api/alertas")
    suspend fun getAllAlertas(): ApiResponse<List<AlertaDto>>
}
