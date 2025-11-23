package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class VacanteResponse(
    val id: String,
    val nombrePerfil: String,
    val area: String,
    val rolLaboral: String,
    val skillsRequeridos: List<SkillReqResponse> = emptyList(),
    val certificacionesRequeridas: List<String>,
    val fechaInicio: String,
    val urgencia: String,
    val estadoVacante: String,
    val creadaPorUsuarioId: String,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val usuarioActualizacion: String
)


data class SkillReqResponse(
    val nombre: String,
    val tipo: String, // TECNICO | BLANDO
    val nivelDeseado: Int,
    val esCritico: Boolean = false
)
