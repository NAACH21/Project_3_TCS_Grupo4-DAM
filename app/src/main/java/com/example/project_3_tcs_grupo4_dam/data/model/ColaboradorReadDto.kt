package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class DisponibilidadDto(
    @SerializedName("estado") val estado: String?,
    @SerializedName("dias") val dias: Int?
)

data class CertificacionDto(
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("fechaObtencion") val fechaObtencion: String?,
    @SerializedName("estado") val estado: String?
)

data class ColaboradorReadDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nombres")
    val nombres: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("area")
    val area: String?,

    // Soporta rolActual (DTO original) o rolLaboral (JSON real)
    @SerializedName("rolActual", alternate = ["rolLaboral"])
    val rolActual: String,

    // Puede venir como null o lista vacía
    @SerializedName("skills")
    val skills: List<ColaboradorSkillDto> = emptyList(),

    @SerializedName("nivelCodigo")
    val nivelCodigo: Int?,

    @SerializedName("certificaciones")
    val certificaciones: List<CertificacionDto> = emptyList(),

    // Puede ser null o venir con otro nombre si es booleano en el JSON original
    @SerializedName("disponibilidad")
    val disponibilidad: DisponibilidadDto?
)
