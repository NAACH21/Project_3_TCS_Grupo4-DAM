package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertasRepository {

    suspend fun obtenerDashboardAdmin(): Result<List<AlertaDashboard>> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.alertasApi.getDashboardAdmin()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Respuesta vacía de alertas"))
                }
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("Error en Alertas API: ${response.code()} ${error ?: ""}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

