package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para la lista de colaboradores (Admin).
 * Se usa cuando el backend devuelve una estructura resumida o con skills como IDs.
 */
data class ColaboradorListDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nombres")
    val nombres: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("area")
    val area: String?,

    @SerializedName("rolLaboral", alternate = ["rolActual"]) // Soporta ambos nombres
    val rol: String?,

    @SerializedName("estado")
    val estado: String?
)
