package com.example.project_3_tcs_grupo4_dam.data.repository

import android.util.Log
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.DashboardData
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class DashboardRepository {

    companion object {
        private const val TAG = "DashboardRepository"
    }

    // Instancia del servicio (asegúrate de haber actualizado RetrofitClient como indiqué arriba)
    private val api = RetrofitClient.dashboardApiService

    suspend fun obtenerMetricasAdmin(): Result<DashboardData> {
        return try {
            Log.d(TAG, "Haciendo petición al endpoint /api/dashboard/metricas-admin")
            val response = api.getMetricasAdmin()

            Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                Log.d(TAG, "Success: ${responseBody.success}, Message: ${responseBody.message}")

                if (responseBody.success) {
                    Log.d(TAG, "Datos recibidos correctamente")
                    Log.d(TAG, "Skills: ${responseBody.data.skillsMasDemandados?.size ?: 0}")
                    Log.d(TAG, "Brechas: ${responseBody.data.brechasPrioritarias?.size ?: 0}")
                    Result.success(responseBody.data)
                } else {
                    Log.e(TAG, "La API retornó success=false: ${responseBody.message}")
                    Result.failure(Exception(responseBody.message))
                }
            } else {
                val errorMsg = "Error en el servidor: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    Log.e(TAG, "Error body: $errorBody")
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener métricas", e)
            Result.failure(e)
        }
    }
}