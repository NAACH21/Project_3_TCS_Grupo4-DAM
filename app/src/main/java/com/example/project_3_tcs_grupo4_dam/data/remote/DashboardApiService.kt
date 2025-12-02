package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.DashboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApiService {
    @GET("api/dashboard/metricas-admin")
    suspend fun getMetricasAdmin(): Response<DashboardResponse>
}