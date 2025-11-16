package com.example.project_3_tcs_grupo4_dam.presentation.auth

import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos.AuthResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos.LoginRequest
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos.RegisterRequest

/**
 * Interfaz para autenticación.
 */
interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<AuthResponse>
    suspend fun register(registerRequest: RegisterRequest): Result<AuthResponse>
    fun token(): String?
}
