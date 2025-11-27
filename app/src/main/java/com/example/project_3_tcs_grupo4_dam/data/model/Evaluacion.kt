package com.example.project_3_tcs_grupo4_dam.data.model

data class Evaluacion(
    val id: String,
    val colaboradorId: String,
    val rolActual: String,
    val liderEvaluador: String,
    val fechaEvaluacion: String,
    val tipoEvaluacion: String,
    val skillsEvaluados: List<SkillEvaluado>,
    val comentarios: String,
    val usuarioResponsable: String,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

data class SkillEvaluado(
    val nombre: String,
    val tipo: String,
    val nivelActual: Int,
    val nivelRecomendado: Int
)
