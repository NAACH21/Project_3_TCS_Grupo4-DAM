package com.example.project_3_tcs_grupo4_dam.data.model

data class VacanteCreateDto(
    val nombrePerfil: String,
    val area: String,
    val rolLaboral: String,
    val skillsRequeridos: List<SkillRequerido>,
    val certificacionesRequeridas: List<String>,
    val fechaInicio: String,
    val urgencia: String,
    val estadoVacante: String,
    val creadaPorUsuarioId: String // Asegúrate de tener este ID disponible
)
