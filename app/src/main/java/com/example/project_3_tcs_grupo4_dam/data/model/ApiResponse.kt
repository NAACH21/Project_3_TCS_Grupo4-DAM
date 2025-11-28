package com.example.project_3_tcs_grupo4_dam.data.model

/**
 * Wrapper genérico para las respuestas del backend .NET
 * Estructura estándar: { "success": bool, "message": string, "data": T }
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)


