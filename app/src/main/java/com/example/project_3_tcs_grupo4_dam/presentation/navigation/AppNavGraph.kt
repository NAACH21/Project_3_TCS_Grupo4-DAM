package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen

// ESTE ARCHIVO PARECE SER REDUNDANTE CON AppNavigation.kt
// Se recomienda usar AppNavigation.kt como entrada principal.
// Si MainActivity llama a este AppNavGraph, debería migrarse.
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorDetalleScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorFormScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradoresScreen
import com.example.project_3_tcs_grupo4_dam.presentation.matching.MatchingScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationDetailScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.NewVacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesDashboardScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.SkillsScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when(role.uppercase()) {
                         "COLABORADOR" -> Routes.COLABORADOR_HOME
                         "ADMIN" -> Routes.ADMIN_HOME
                         "MANAGER" -> Routes.MANAGER_HOME
                         else -> Routes.LOGIN
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        
        composable(Routes.REGISTER) {
            // TODO: Implementar pantalla de registro
            SimplePlaceholderScreen(title = "Registro")
        }

        composable(Routes.COLABORADORES) {
            ColaboradoresScreen(navController)
        }

        composable(
            route = "${Routes.COLABORADOR_DETALLE}/{colaboradorId}",
            arguments = listOf(
                navArgument("colaboradorId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val colaboradorId = backStackEntry.arguments?.getString("colaboradorId")!!
            ColaboradorDetalleScreen(
                colaboradorId = colaboradorId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COLABORADOR_FORM) {
            ColaboradorFormScreen(navController)
        }

        composable(
            route = "${Routes.COLABORADOR_FORM}/{colaboradorId}",
            arguments = listOf(
                navArgument("colaboradorId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            ColaboradorFormScreen(navController)
        }

        // Rutas adicionales
        composable(Routes.SKILLS) {
            SkillsScreen(navController = navController)
        }

        composable(Routes.NIVEL_SKILLS) {
            SkillsScreen(navController = navController)
        }

        // --- NOTIFICACIONES DASHBOARD (Usa API: api/alertas/dashboard/admin) ---
        // FIX: Actualizado a NOTIFICACIONES_ADMIN
        composable(Routes.NOTIFICACIONES_ADMIN) {
            NotificacionesDashboardScreen(navController = navController)
        }

        // RUTAS DE EVALUACIONES
        composable(Routes.EVALUACIONES) {
            EvaluationScreen(navController = navController)
        }
        composable(Routes.EVALUATION_SCREEN) {
            EvaluationScreen(navController = navController)
        }
        composable(Routes.EVALUATION_HISTORY) {
            EvaluationHistoryScreen(navController = navController)
        }
        composable(
            route = Routes.EVALUATION_DETAIL,
            arguments = listOf(navArgument("evaluationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId") ?: ""
            EvaluationDetailScreen(
                evaluationId = evaluationId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.BULK_UPLOAD) {
            BulkUploadScreen(onBackClick = { navController.popBackStack() })
        }

        // RUTA DE VACANTES
        composable(Routes.VACANTES) {
            VacantScreen(navController = navController)
        }

        composable(Routes.VACANTES_COLABORADOR) {
            VacantesScreen(navController = navController)
        }

        composable("newVacancy") {
            NewVacantScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            SimplePlaceholderScreen(title = "Dashboard")
        }

        composable(Routes.MATCHING) {
            MatchingScreen(navController)
        }
    }
}

@Composable
private fun SimplePlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $title")
    }
}
