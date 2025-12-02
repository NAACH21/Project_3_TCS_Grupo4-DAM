package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse

import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.VacanteApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VacanteRepository(private val vacanteApiService: VacanteApiService) {
    suspend fun getVacantes(): List<VacanteResponse> {
        return vacanteApiService.getVacantes()
    }

    /**
     * Obtiene vacantes filtradas por estado activo
     */
    suspend fun getVacantes(activa: Boolean?): List<VacanteResponse> = withContext(Dispatchers.IO) {
        vacanteApiService.getVacantes(activa)
    }

    /**
     * Crea una nueva vacante y retorna el objeto con ID generado
     */
    suspend fun createVacante(vacante: VacanteCreateDto): Result<Vacante> = withContext(Dispatchers.IO) {
        try {
            val vacanteCreada = vacanteApiService.createVacante(vacante)
            Result.success(vacanteCreada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}