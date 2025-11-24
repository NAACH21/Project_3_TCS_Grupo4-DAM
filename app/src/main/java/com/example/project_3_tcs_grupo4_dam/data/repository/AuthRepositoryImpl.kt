package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.remote.AuthApiService
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Implementación del repositorio de autenticación
 * Maneja la comunicación con la API y el almacenamiento local
 */
class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    /**
     * Decodifica el payload del JWT para extraer claims (uid, cid, etc.)
     */
    private fun decodeJwtPayload(token: String): JSONObject? {
        try {
            val parts = token.split(".")
            if (parts.size != 3) {
                android.util.Log.e("AuthRepository", "Token JWT inválido: no tiene 3 partes")
                return null
            }

            // La segunda parte es el payload (claims)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            android.util.Log.d("AuthRepository", "JWT Payload decodificado: $decodedString")

            return JSONObject(decodedString)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error al decodificar JWT", e)
            return null
        }
    }

    override suspend fun login(request: AuthDtos.LoginRequest): Result<ApiResponse<AuthDtos.LoginResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        android.util.Log.d("AuthRepository", "=== RESPUESTA DE LOGIN ===")
                        android.util.Log.d("AuthRepository", "Success: ${apiResponse.success}")
                        android.util.Log.d("AuthRepository", "Data: ${apiResponse.data}")

                        // Si el login fue exitoso, guardar la sesión
                        if (apiResponse.success && apiResponse.data != null) {
                            val token = apiResponse.data.token
                            val colaboradorId = apiResponse.data.colaboradorId
                            val username = apiResponse.data.username
                            val rolSistema = apiResponse.data.rolSistema

                            android.util.Log.d("AuthRepository", "Token: ${token.take(50)}...")
                            android.util.Log.d("AuthRepository", "Username: $username")
                            android.util.Log.d("AuthRepository", "Rol: $rolSistema")
                            android.util.Log.d("AuthRepository", "ColaboradorId: $colaboradorId")

                            // ⭐ EXTRAER usuarioId del JWT (claim "uid") ⭐
                            val jwtPayload = decodeJwtPayload(token)
                            val usuarioId = jwtPayload?.optString("uid", "") ?: ""

                            android.util.Log.d("AuthRepository", "=== JWT DECODIFICADO ===")
                            android.util.Log.d("AuthRepository", "uid (usuarioId): $usuarioId")
                            android.util.Log.d("AuthRepository", "cid (colaboradorId en JWT): ${jwtPayload?.optString("cid", "")}")

                            // ⭐ VALIDACIÓN CRÍTICA: El token DEBE contener uid ⭐
                            if (usuarioId.isBlank()) {
                                android.util.Log.e("AuthRepository", "❌ ERROR CRÍTICO: El JWT NO contiene el claim 'uid' (usuarioId)")
                                android.util.Log.e("AuthRepository", "Payload completo: $jwtPayload")
                                return@withContext Result.failure(
                                    Exception("El token no contiene el ID de usuario (uid). Contacta al administrador.")
                                )
                            }

                            sessionManager.saveSession(
                                token = token,
                                rolSistema = rolSistema,
                                colaboradorId = colaboradorId,
                                username = username,
                                usuarioId = usuarioId
                            )

                            android.util.Log.d("AuthRepository", "✅ Sesión guardada correctamente con usuarioId: $usuarioId")
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
                android.util.Log.e("AuthRepository", "Excepción en login", e)
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

    override fun getToken(): String? = sessionManager.getToken()

    override fun getRol(): String? = sessionManager.getRol()

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    override fun logout() = sessionManager.clearSession()

    override fun getSessionManager(): SessionManager = sessionManager
}
