package com.example.project_3_tcs_grupo4_dam.data.repository

import android.util.Log
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.model.AnuncioVacanteRequest
import com.example.project_3_tcs_grupo4_dam.data.remote.AlertasApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar las notificaciones del Dashboard
 */
class NotificacionesRepository(private val alertasApiService: AlertasApiService) {

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
     * Envía anuncio de vacante disponible.
     * Ahora incluye limpieza de ID y logging detallado de errores 400.
     */
    suspend fun anunciarVacante(vacanteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Limpieza preventiva del ID (elimina espacios o saltos de línea invisibles)
            val cleanId = vacanteId.trim()
            
            if (cleanId.isBlank()) {
                return@withContext Result.failure(Exception("El ID de la vacante está vacío"))
            }

            Log.d("NotificacionesRepo", "Enviando anuncio para vacanteId: '$cleanId'")

            // 2. Crear Request estrictamente con String
            val request = AnuncioVacanteRequest(cleanId)
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
                // 3. CAPTURA DEL ERROR REAL DEL SERVIDOR
                // Esto es vital para entender por qué da 400 (ej. "vacanteId inválido", "vacante ya anunciada", etc.)
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Log.e("NotificacionesRepo", "Error ${response.code()} al anunciar. Body: $errorBody")
                
                Result.failure(
                    Exception("Error ${response.code()}: $errorBody")
                )
            }
        } catch (e: Exception) {
            Log.e("NotificacionesRepo", "Excepción al anunciar vacante", e)
            Result.failure(e)
        }
    }
}
