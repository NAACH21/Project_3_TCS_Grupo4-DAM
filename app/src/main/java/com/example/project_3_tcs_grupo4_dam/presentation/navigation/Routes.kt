package com.example.project_3_tcs_grupo4_dam.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val COLABORADOR_FORM = "colaborador_form"
    const val MATCHING = "matching"

    // Nuevas rutas para la barra inferior
    const val SKILLS = "skills"
    const val EVALUACIONES = "evaluaciones"
    const val VACANTES = "vacantes"
    const val DASHBOARD = "dashboard"

    // Rutas de evaluaciones
    const val EVALUATIONS_HISTORY = "evaluations_history"
    const val BULK_UPLOAD = "bulk_upload"
    const val EVALUATION_DETAIL = "evaluation_detail"

    fun evaluationDetail(id: String) = "evaluation_detail/$id"
}
