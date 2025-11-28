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
    const val VACANTES_COLABORADOR = "vacantes_colaborador"
    const val DASHBOARD = "dashboard"

    // Rutas de evaluaciones
    const val EVALUATION_SCREEN = "evaluation"
    const val EVALUATION_HISTORY = "evaluation_history"
    const val EVALUATION_DETAIL = "evaluation_detail/{evaluationId}"
    const val BULK_UPLOAD = "bulk_upload"

    // Ruta de notificaciones
    const val NOTIFICACIONES = "notificaciones"

    // Ruta de administración de skills
    const val NIVEL_SKILLS = "nivel_skills"

    // Helper para navegar a detalle
    fun evaluationDetail(evaluationId: String) = "evaluation_detail/$evaluationId"
}
