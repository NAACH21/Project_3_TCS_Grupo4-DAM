package com.example.project_3_tcs_grupo4_dam.data.model

data class EvaluacionCreateDto(
    val colaboradorId: String,
    val rolActual: String,
    val liderEvaluador: String,
    val fechaEvaluacion: String,
    val tipoEvaluacion: String,
    val skillsEvaluados: List<SkillEvaluadoCreateDto>,
    val comentarios: String,
    val usuarioResponsable: String
)