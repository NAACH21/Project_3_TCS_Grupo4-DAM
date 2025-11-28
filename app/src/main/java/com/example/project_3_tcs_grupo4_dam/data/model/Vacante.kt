package com.example.project_3_tcs_grupo4_dam.data.model

data class Vacante(
    val id: String,
    val nombrePerfil: String,
    val area: String,
    val rolLaboral: String,
    val skillsRequeridos: List<SkillRequerido>,
    val certificacionesRequeridas: List<String>,
    val fechaInicio: String,
    val urgencia: String,
    val estadoVacante: String,
    val creadaPorUsuarioId: String,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val usuarioActualizacion: String
)