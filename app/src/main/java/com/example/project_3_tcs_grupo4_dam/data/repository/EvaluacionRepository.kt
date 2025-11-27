package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.Evaluacion
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.toEvaluacion
import com.example.project_3_tcs_grupo4_dam.data.remote.EvaluacionApiService

class EvaluacionRepository(
    private val evaluacionApiService: EvaluacionApiService
) {

    suspend fun getEvaluaciones(): List<Evaluacion> {
        return evaluacionApiService.getEvaluaciones().map { it.toEvaluacion() }
    }

    suspend fun getEvaluacionById(id: String): Evaluacion {
        return evaluacionApiService.getEvaluacionById(id).toEvaluacion()
    }

    suspend fun createEvaluacion(body: EvaluacionCreateDto): Evaluacion {
        return evaluacionApiService.createEvaluacion(body).toEvaluacion()
    }

}
