package com.example.project_3_tcs_grupo4_dam.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val HOME_COLABORADOR = "home_colaborador"
    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val COLABORADOR_FORM = "colaborador_form"
    const val MATCHING = "matching"

    // Nuevas rutas para la barra inferior
    const val SKILLS = "skills"
    const val COLABORADOR_SKILLS = "colaborador_skills" // Nueva ruta específica
    const val EVALUACIONES = "evaluaciones"
    const val VACANTES = "vacantes"
    const val VACANTES_COLABORADOR = "vacantes_colaborador"
    const val DASHBOARD = "dashboard"

    // Rutas de evaluaciones
    const val EVALUATION_SCREEN = "evaluation_screen"
    const val EVALUATIONS_HISTORY = "evaluations_history"
    const val BULK_UPLOAD = "bulk_upload"
    const val EVALUATION_DETAIL = "evaluation_detail"

    // Ruta de notificaciones
    const val NOTIFICACIONES = "notificaciones"

    // Ruta de administración de skills
    const val NIVEL_SKILLS = "nivel_skills"
    
    // Ruta para actualizar skill
    const val ACTUALIZAR_SKILL = "actualizar_skill"

    fun evaluationDetail(id: String) = "evaluation_detail/$id"
    
    // Función helper para la navegación con argumentos
    fun actualizarSkill(colaboradorId: String, skillName: String) = "$ACTUALIZAR_SKILL/$colaboradorId/$skillName"
}
