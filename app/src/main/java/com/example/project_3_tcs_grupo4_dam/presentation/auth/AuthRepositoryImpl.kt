package com.example.project_3_tcs_grupo4_dam.presentation.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.remote.AuthApiService

/**
 * Clase sellada para manejar resultados de operaciones
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

/**
 * Implementación del repositorio de autenticación
 */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    context: Context
) : AuthRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override suspend fun login(loginRequest: AuthDtos.LoginRequest): Result<AuthDtos.AuthResponse> {
        return try {
            val response = api.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardar token si el login fue exitoso
                if (authResponse.success && authResponse.data != null) {
                    saveToken(authResponse.data.token)
                }
                Result.Success(authResponse)
            } else {
                Result.Error(Exception("Error en el login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun register(registerRequest: AuthDtos.RegisterRequest): Result<AuthDtos.AuthResponse> {
        return try {
            val response = api.register(registerRequest)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardar token si el registro fue exitoso
                if (authResponse.success && authResponse.data != null) {
                    saveToken(authResponse.data.token)
                }
                Result.Success(authResponse)
            } else {
                Result.Error(Exception("Error en el registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error de conexión: ${e.message}"))
        }
    }

    override fun token(): String? {
        return prefs.getString("token", null)
    }

    private fun saveToken(token: String) {
        prefs.edit {
            putString("token", token)
        }
    }
}
