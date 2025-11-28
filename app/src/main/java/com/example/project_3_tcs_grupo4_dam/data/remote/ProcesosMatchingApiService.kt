package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ProcesoMatchingRequest
import com.example.project_3_tcs_grupo4_dam.data.model.ProcesoMatchingResponse
import retrofit2.http.*

interface ProcesosMatchingApiService {
    @GET("api/procesos_matching")
    suspend fun getProcesos(): List<ProcesoMatchingResponse>

    @POST("api/procesos_matching")
    suspend fun createProceso(@Body proceso: ProcesoMatchingRequest): ProcesoMatchingResponse

    @GET("api/procesos_matching/{id}")
    suspend fun getProcesoById(@Path("id") id: String): ProcesoMatchingResponse

    @GET("api/procesos_matching/vacante/{vacanteId}")
    suspend fun getProcesosByVacante(@Path("vacanteId") vacanteId: String): List<ProcesoMatchingResponse>
}