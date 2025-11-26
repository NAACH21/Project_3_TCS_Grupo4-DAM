package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos

/**
 * Interfaz del repositorio de autenticación
 * Define el contrato para las operaciones de autenticación
 */
interface AuthRepository {

    /**
     * Realiza el login del usuario
     * @return Result con la respuesta del servidor
     */
    suspend fun login(request: AuthDtos.LoginRequest): Result<ApiResponse<AuthDtos.LoginResponse>>

    /**
     * Registra un nuevo usuario
     * @return Result con la respuesta del servidor
     */
    suspend fun register(request: AuthDtos.RegisterRequest): Result<ApiResponse<AuthDtos.RegisterResponse>>


    /**
     * Obtiene el token almacenado
     */
    fun getToken(): String?

    /**
     * Obtiene el rol del usuario
     */
    fun getRol(): String?
    fun getUsername(): String?

    /**
     * Verifica si hay sesión activa
     */
    fun isLoggedIn(): Boolean

    /**
     * Cierra la sesión
     */
    fun logout()
}

