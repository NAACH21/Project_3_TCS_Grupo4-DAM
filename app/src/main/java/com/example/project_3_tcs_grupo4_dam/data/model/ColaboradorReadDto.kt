package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class DisponibilidadDto(
    @SerializedName("estado") val estado: String,
    @SerializedName("dias") val dias: Int
)

data class CertificacionDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("fechaObtencion") val fechaObtencion: String?,
    @SerializedName("estado") val estado: String
)

data class ColaboradorReadDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nombres")
    val nombres: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("area")
    val area: String,

    @SerializedName("rolActual")
    val rolActual: String,

    // Ids de la colección skills
    @SerializedName("skills")
    val skills: List<String>,

    // Código del nivel (0, 1, 2, 3…)
    @SerializedName("nivelCodigo")
    val nivelCodigo: Int?,

    // Certificaciones embebidas
    @SerializedName("certificaciones")
    val certificaciones: List<CertificacionDto>,

    // Disponibilidad embebida
    @SerializedName("disponibilidad")
    val disponibilidad: DisponibilidadDto
)
