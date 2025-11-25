package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

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
        @SerializedName("id", alternate = ["_id", "userId"])
        val id: String?, 
        
        val token: String,
        val username: String,
        val rolSistema: String,
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

// --- CLASES DE NIVEL SUPERIOR PARA EVITAR PROBLEMAS DE RESOLUCIÓN ---

data class RoleDto(
    @SerializedName("id", alternate = ["_id"])
    val id: String,
    val nombreRol: String
)

data class UsuarioDto(
    @SerializedName("id", alternate = ["_id"])
    val id: String,
    val username: String,
    val rolId: String
)
