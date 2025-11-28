package com.example.project_3_tcs_grupo4_dam.data.model

// Respuesta mínima para la carga masiva de evaluaciones.
// Ajusta los campos si la API devuelve una estructura diferente.
data class BulkUploadResponse(
    val success: Boolean,
    val message: String? = null,
    val createdCount: Int = 0
)

