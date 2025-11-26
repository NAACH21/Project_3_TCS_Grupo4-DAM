package com.example.project_3_tcs_grupo4_dam.data.model

/**
 * DTOs para el módulo de autenticación
 */
object AuthDtos {

    /**
     * Request para el login
     */
    data class LoginRequest(
        val username: String,
        val password: String
    )

    /**
     * Response del login (dentro del campo "data")
     */
    data class LoginResponse(
        val token: String,
        val username: String,
        val rolSistema: String, // "ADMIN", "BUSINESS_MANAGER", "COLABORADOR"
        val colaboradorId: String?
    )

    /**
     * Request para el registro
     */
    data class RegisterRequest(
        val nombreCompleto: String,
        val email: String,
        val password: String
    )

    /**
     * Response del registro
     */
    data class RegisterResponse(
        val userId: String,
        val username: String
    )
}

