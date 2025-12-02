package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.model.AnuncioVacanteRequest
import com.example.project_3_tcs_grupo4_dam.data.remote.AlertasApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar las notificaciones del Dashboard
 */
class NotificacionesRepository(private val alertasApiService: AlertasApiService) {

    /**
     * Obtiene las alertas del dashboard según el tipo de usuario
     * @param esAdmin Si es true, obtiene dashboard de admin, si es false obtiene del colaborador
     * @param userId ID del usuario colaborador (obligatorio si esAdmin = false)
     * @return Result con lista de AlertaDashboard o error
     */
    suspend fun obtenerDashboard(
        esAdmin: Boolean,
        userId: String?
    ): Result<List<AlertaDashboard>> = withContext(Dispatchers.IO) {
        try {
            val response = if (esAdmin) {
                alertasApiService.getDashboardAdmin()
            } else {
                if (userId.isNullOrBlank()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("userId es requerido para colaboradores")
                    )
                }
                alertasApiService.getDashboardColaborador(userId)
            }

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(
                        Exception(apiResponse?.message ?: "Error al obtener dashboard")
                    )
                }
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}: ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envía anuncio de vacante disponible
     * @param vacanteId ID de la vacante a anunciar
     * @return Result<Unit> indicando éxito o error
     */
    suspend fun anunciarVacante(vacanteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = AnuncioVacanteRequest(vacanteId)
            val response = alertasApiService.anunciarVacante(request)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(
                        Exception(apiResponse?.message ?: "Error al anunciar vacante")
                    )
                }
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}: ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

