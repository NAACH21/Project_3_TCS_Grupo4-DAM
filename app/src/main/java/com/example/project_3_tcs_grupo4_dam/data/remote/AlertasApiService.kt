package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.CreateAlertaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AlertasApiService {

    @GET("api/alertas")
    suspend fun getAllAlertas(): ApiResponse<List<AlertaDto>>

    @POST("api/alertas")
    suspend fun createAlerta(
        @Body body: CreateAlertaRequest
    ): ApiResponse<AlertaDto>
}
