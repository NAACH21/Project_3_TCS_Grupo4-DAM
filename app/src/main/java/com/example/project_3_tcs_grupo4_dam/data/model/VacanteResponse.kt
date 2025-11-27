package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class VacanteResponse(
    @SerializedName("_id") val _id: IdWrapper?,
    @SerializedName("id") val id: String?,
    val nombrePerfil: String,
    val area: String,
    val rolLaboral: String?,
    val skillsRequeridos: List<SkillReqResponse> = emptyList(),
    val certificacionesRequeridas: List<String> = emptyList(),
    val fechaInicio: String?,
    val urgencia: String?,
    val estadoVacante: String?,
    val creadaPorUsuarioId: String?,
    val fechaCreacion: String?,
    val fechaActualizacion: String?,
    val usuarioActualizacion: String?
) {
    // Helper para obtener el id como String (normaliza {$oid} o id simple)
    fun getIdValue(): String = _id?.value() ?: id ?: ""
}


data class SkillReqResponse(
    val nombre: String,
    val tipo: String, // TECNICO | BLANDO
    val nivelDeseado: Int,
    val esCritico: Boolean = false
)
