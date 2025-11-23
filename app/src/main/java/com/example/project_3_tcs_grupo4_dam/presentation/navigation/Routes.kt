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

    // --- CONSTANTES PARA BOTTOM BAR ADMIN (Alias) ---
    // Estas constantes se usan en BottomNavBar.kt, así que las mapeamos a las rutas de Admin
    const val HOME = ADMIN_HOME 
    const val VACANTES = "vacantes_admin"             
    const val EVALUACIONES = "evaluaciones_admin"     
    const val DASHBOARD = "dashboard_admin"

    // --- VACANTES (ESPECÍFICAS) ---
    const val VACANTES_COLABORADOR = "vacantes_colaborador" // Diseño Job Match
    const val VACANTES_ADMIN = "vacantes_admin"             // Diseño Gestión (Lista simple)
    const val NEW_VACANT = "new_vacant"                     // Crear vacante (Admin)

    // --- GESTIÓN (ADMIN/MANAGER) ---
    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val COLABORADOR_FORM = "colaborador_form"
    
    const val MATCHING = "matching"
    const val DASHBOARD_ADMIN = "dashboard_admin" // Alias explícito
    
    // --- EVALUACIONES ---
    const val EVALUACIONES_ADMIN = "evaluaciones_admin" // Alias explícito
    const val NUEVA_EVALUACION = "nueva_evaluacion"     
    const val EVALUATION_DETAIL = "evaluation_detail"
    const val BULK_UPLOAD = "bulk_upload"
    const val EVALUATION_SCREEN = "evaluation_screen" // Alias

    // --- SKILLS Y NIVELES ---
    const val SKILLS_ADMIN = "skills_gap_admin" 
    const val SKILLS = "skills_gap_admin" // Alias para botones de home
    const val NIVEL_SKILLS = "nivel_skills"

    // --- COLABORADOR ESPECÍFICO ---
    const val COLABORADOR_SKILLS = "colaborador_skills"
    const val ALERTAS_COLABORADOR = "alertas_colaborador"
    const val ACTUALIZAR_SKILL_BASE = "skill_details_screen"
    const val ACTUALIZAR_SKILL = "skill_details_screen" // Alias para AppNavigation

    // --- ALERTAS ADMIN ---
    const val ALERTAS_ADMIN = "alertas_admin"
    const val NOTIFICACIONES = "notificaciones" // Alias compartido o genérico

    // --- HELPERS DE NAVEGACIÓN ---
    
    fun evaluationDetail(id: String) = "$EVALUATION_DETAIL/$id"

    fun actualizarSkill(colaboradorId: String, skillName: String): String {
        return "$ACTUALIZAR_SKILL_BASE/$colaboradorId/${Uri.encode(skillName)}"
    }
}
