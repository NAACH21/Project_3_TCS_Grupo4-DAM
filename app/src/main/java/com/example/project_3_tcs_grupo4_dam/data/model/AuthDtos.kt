package com.example.project_3_tcs_grupo4_dam.data.model

/**
 * DTOs para autenticación basados en la respuesta del backend
 */
object AuthDtos {

    // Request para login
    data class LoginRequest(
        val email: String,
        val password: String
    )

    // Request para registro
    data class RegisterRequest(
        val nombreCompleto: String,
        val email: String,
        val password: String
    )

    // Respuesta de autenticación del backend
    data class AuthResponse(
        val success: Boolean,
        val message: String,
        val data: AuthData?
    )

    // Data contenida en la respuesta exitosa
    data class AuthData(
        val token: String,
        val refreshToken: String,
        val tokenExpires: String,
        val user: UserResponse
    )

    // Información del usuario
    data class UserResponse(
        val id: String,
        val nombreCompleto: String,
        val email: String,
        val roles: List<String>
    )
}
