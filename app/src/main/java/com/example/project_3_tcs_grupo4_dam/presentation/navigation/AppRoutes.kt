package com.example.project_3_tcs_grupo4_dam.presentation.navigation

/**
 * Rutas de navegación de la aplicación
 * Define las pantallas principales según el rol del usuario
 */
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Rutas por rol
    const val ADMIN_HOME = "admin_home"
    const val MANAGER_HOME = "manager_home"
    const val COLABORADOR_HOME = "colaborador_home"

    /**
     * Retorna la ruta de inicio según el rol del usuario
     */
    fun getHomeRouteByRole(role: String): String {
        return when (role.uppercase()) {
            "ADMIN" -> ADMIN_HOME
            "BUSINESS_MANAGER" -> MANAGER_HOME
            "COLABORADOR" -> COLABORADOR_HOME
            else -> LOGIN
        }
    }
}

