package com.example.project_3_tcs_grupo4_dam.presentation.navigation

sealed class Routes(val route: String) {
    object LoginScreen : Routes("login")
    object RegisterScreen : Routes("register")
    object HomeScreen : Routes("home")
    object MainScreen : Routes("main")
    object SplashScreen : Routes("splash")
    object ColaboradorListScreen : Routes("colaborador_list")
    object ColaboradorDetailScreen : Routes("colaborador_detail/{colaboradorId}")
    object ColaboradorFormScreen : Routes("colaborador_form")
    object EvaluationScreen: Routes("evaluation")
    object EvaluationHistoryScreen: Routes("evaluation_history")
    object EvaluationDetailScreen: Routes("evaluation_detail/{evaluationId}")
    object VacantesScreen : Routes("vacantes")
    object DashboardScreen : Routes("dashboard")
    object NotificacionesScreen : Routes("notificaciones")
    object MatchingScreen : Routes("matching")
    object BulkUploadScreen : Routes("bulk_upload")
    object NivelSkillsScreen : Routes("nivel_skills")
    object SkillsScreen : Routes("skills")
    object VacantesColaboradorScreen : Routes("vacantes_colaborador")

    fun withId(id: String): String {
        return "$route/$id"
    }
}