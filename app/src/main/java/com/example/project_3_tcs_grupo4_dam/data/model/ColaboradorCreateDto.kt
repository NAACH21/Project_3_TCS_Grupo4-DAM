package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class CertificacionCreateDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("fechaObtencion") val fechaObtencion: String?
)

data class DisponibilidadCreateDto(
    @SerializedName("estado") val estado: String,
    @SerializedName("dias") val dias: Int?
)

data class ColaboradorCreateDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("area") val area: String,
    @SerializedName("rolActual") val rolActual: String,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("nivelCodigo") val nivelCodigo: Int?,
    @SerializedName("certificaciones") val certificaciones: List<CertificacionCreateDto>,
    @SerializedName("disponibilidad") val disponibilidad: DisponibilidadCreateDto
)

