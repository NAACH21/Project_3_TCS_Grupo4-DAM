package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.BulkUploadResponse
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionReadDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EvaluacionApiService {

    @POST("api/evaluaciones")
    suspend fun createEvaluacion(@Body evaluacion: EvaluacionCreateDto): EvaluacionReadDto

    @POST("api/evaluaciones/bulk")
    suspend fun createEvaluationsBulk(@Body evaluations: List<EvaluacionCreateDto>): BulkUploadResponse

    @GET("api/evaluaciones")
    suspend fun getEvaluaciones(): List<EvaluacionReadDto>

    // Función añadida para obtener una evaluación por su ID
    @GET("api/evaluaciones/{id}")
    suspend fun getEvaluacionById(@Path("id") id: String): EvaluacionReadDto

}
