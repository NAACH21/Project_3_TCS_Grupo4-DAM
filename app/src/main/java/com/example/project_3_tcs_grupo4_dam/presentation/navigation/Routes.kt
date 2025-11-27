package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import android.net.Uri

object Routes {

    // --- AUTENTICACIÓN ---
    const val LOGIN = "login"
    const val REGISTER = "register"

    // --- HOMES ---
    const val COLABORADOR_HOME = "home_colaborador"
    const val ADMIN_HOME = "home_admin"
    const val MANAGER_HOME = "home_manager"

    // Alias para Home general (Admin)
    const val HOME = ADMIN_HOME


    // ===============================
    //        VACANTES
    // ===============================

    const val VACANTES_ADMIN = "vacantes_admin"     // Vista admin
    const val VACANTES_COLABORADOR = "vacantes_colaborador" // Vista colaborador
    const val NEW_VACANT = "new_vacant"             // Crear vacante

    // Alias general para bottom bar
    const val VACANTES = VACANTES_ADMIN



    // ===============================
    //        COLABORADORES
    // ===============================

    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val COLABORADOR_FORM = "colaborador_form"

    const val COLABORADOR_SKILLS = "colaborador_skills"
    const val SOLICITUDES_COLABORADOR = "solicitudes_colaborador"
    const val SOLICITUD_CERTIFICACION_COLABORADOR = "solicitud_certificacion_colaborador"
    const val SOLICITUD_SKILLS_COLABORADOR = "solicitud_skills_colaborador"

    const val ALERTAS_COLABORADOR = "alertas_colaborador"



    // ===============================
    //        MATCHING
    // ===============================

    const val MATCHING = "matching"



    // ===============================
    //       SKILLS & GAP ANALYSIS
    // ===============================

    const val SKILLS_ADMIN = "skills_gap_admin"  // Vista admin
    const val SKILLS = SKILLS_ADMIN              // Alias general

    const val NIVEL_SKILLS = "nivel_skills"

    // Pantalla para editar un skill individual
    const val ACTUALIZAR_SKILL_BASE = "skill_details_screen"
    const val ACTUALIZAR_SKILL = ACTUALIZAR_SKILL_BASE



    // ===============================
    //         EVALUACIONES
    // ===============================

    const val EVALUACIONES_ADMIN = "evaluaciones_admin"  // Vista admin
    const val EVALUACIONES = EVALUACIONES_ADMIN           // Alias general

    const val EVALUATION_SCREEN = "evaluation_screen"     // Pantalla principal
    const val NUEVA_EVALUACION = "nueva_evaluacion"
    const val BULK_UPLOAD = "bulk_upload"

    const val EVALUATION_DETAIL = "evaluation_detail"

    // (De tu rama fix) historial de evaluaciones
    const val EVALUATIONS_HISTORY = "evaluations_history"



    // ===============================
    //       SOLICITUDES (ADMIN)
    // ===============================

    const val SOLICITUDES_ADMIN = "solicitudes_admin"
    const val NUEVA_ENTREVISTA_ADMIN = "nueva_entrevista_admin"



    // ===============================
    //         DASHBOARD
    // ===============================

    const val DASHBOARD_ADMIN = "dashboard_admin"
    const val DASHBOARD = DASHBOARD_ADMIN  // Alias general



    // ===============================
    //        NOTIFICACIONES
    // ===============================

    const val ALERTAS_ADMIN = "alertas_admin"
    const val NOTIFICACIONES = "notificaciones"



    // ===============================
    //       HELPERS DINÁMICOS
    // ===============================

    fun evaluationDetail(id: String) = "$EVALUATION_DETAIL/$id"

    fun actualizarSkill(colaboradorId: String, skillName: String): String {
        return "$ACTUALIZAR_SKILL_BASE/$colaboradorId/${Uri.encode(skillName)}"
    }
}
