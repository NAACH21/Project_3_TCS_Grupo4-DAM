package com.example.project_3_tcs_grupo4_dam.data.model

fun EvaluacionReadDto.toEvaluacion(): Evaluacion {
    return Evaluacion(
        id = this.id,
        colaboradorId = this.colaboradorId,
        rolActual = this.rolActual,
        liderEvaluador = this.liderEvaluador,
        fechaEvaluacion = this.fechaEvaluacion,
        tipoEvaluacion = this.tipoEvaluacion,
        skillsEvaluados = this.skillsEvaluados.map { it.toSkillEvaluado() },
        comentarios = this.comentarios,
        usuarioResponsable = this.usuarioResponsable,
        fechaCreacion = this.fechaCreacion,
        fechaActualizacion = this.fechaActualizacion
    )
}