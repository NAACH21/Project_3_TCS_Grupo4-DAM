package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.model.AnuncioVacanteRequest
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlertasApiService {

    @GET("api/alertas")
    suspend fun getAllAlertas(): ApiResponse<List<AlertaDto>>

    /**
     * Obtiene el dashboard de notificaciones para administradores
     */
    @GET("api/alertas/dashboard/admin")
    suspend fun getDashboardAdmin(): Response<ApiResponse<List<AlertaDashboard>>>

    /**
     * Obtiene el dashboard de notificaciones para un colaborador específico
     */
    @GET("api/alertas/dashboard/colaborador/{id}")
    suspend fun getDashboardColaborador(@Path("id") id: String): Response<ApiResponse<List<AlertaDashboard>>>

    /**
     * Envía anuncio de vacante disponible a colaboradores elegibles
     */
    @POST("api/alertas/dashboard/anunciar-vacante")
    suspend fun anunciarVacante(@Body request: AnuncioVacanteRequest): Response<ApiResponse<Unit>>
}
