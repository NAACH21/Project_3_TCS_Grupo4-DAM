package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.remote.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de autenticación
 * Maneja la comunicación con la API y el almacenamiento local
 */
class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: AuthDtos.LoginRequest): Result<ApiResponse<AuthDtos.LoginResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        // Si el login fue exitoso, guardar la sesión
                        if (apiResponse.success && apiResponse.data != null) {
                            sessionManager.saveSession(
                                token = apiResponse.data.token,
                                rolSistema = apiResponse.data.rolSistema,
                                colaboradorId = apiResponse.data.colaboradorId,
                                username = apiResponse.data.username
                            )
                        }
                        Result.success(apiResponse)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Result.failure(Exception("Error ${response.code()}: $errorMsg"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(request: AuthDtos.RegisterRequest): Result<ApiResponse<AuthDtos.RegisterResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        Result.success(apiResponse)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Result.failure(Exception("Error ${response.code()}: $errorMsg"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override fun getUsername(): String? = sessionManager.getUsername()

    override fun getToken(): String? = sessionManager.getToken()

    override fun getRol(): String? = sessionManager.getRol()

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    override fun logout() = sessionManager.clearSession()
}

