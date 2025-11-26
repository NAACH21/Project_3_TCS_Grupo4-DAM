package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

// Estructura de la skill DENTRO del ColaboradorReadDto
data class ColaboradorSkillDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("esCritico") val esCritico: Boolean
)

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

    // Corregido: El nombre del campo y la anotación ahora coinciden con el JSON de la API
    @SerializedName("rolLaboral")
    val rolLaboral: String,

    // Corregido: Ahora es una lista de objetos ColaboradorSkillDto
    @SerializedName("skills")
    val skills: List<ColaboradorSkillDto>,

    @SerializedName("nivelCodigo")
    val nivelCodigo: Int?,

    @SerializedName("certificaciones")
    val certificaciones: List<CertificacionDto>,

    @SerializedName("disponibilidad")
    val disponibilidad: DisponibilidadDto
)
