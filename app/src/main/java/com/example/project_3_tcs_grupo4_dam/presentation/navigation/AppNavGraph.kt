package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorDetalleScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorFormScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradoresScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.matching.MatchingScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationDetailScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.NewVacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.SkillsScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorHomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudColaboradorScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // HOME
        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        // LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = viewModel,
                onLoginSuccess = { role ->
                    val homeRoute = when (role.uppercase()) {
                        "ADMIN" -> Routes.HOME
                        // Se normaliza BUSINESS_MANAGER a HOME para evitar dependencia de rutas específicas
                        "BUSINESS_MANAGER" -> Routes.HOME
                        "COLABORADOR" -> Routes.COLABORADOR_HOME
                        else -> Routes.HOME
                    }
                    navController.navigate(homeRoute) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {}
            )
        }

        // COLABORADORES
        composable(Routes.COLABORADORES) {
            ColaboradoresScreen(navController)
        }

        composable(
            route = "${Routes.COLABORADOR_DETALLE}/{colaboradorId}",
            arguments = listOf(
                navArgument("colaboradorId") { type = NavType.StringType }
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

        // SKILLS
        composable(Routes.SKILLS) {
            SkillsScreen(navController = navController)
        }

        composable(Routes.NIVEL_SKILLS) {
            SkillsScreen(navController = navController)
        }

        // NOTIFICACIONES
        composable(Routes.NOTIFICACIONES) {
            NotificacionesScreen(navController = navController)
        }

        // EVALUACIONES
        composable(Routes.EVALUATION_SCREEN) {
            EvaluationScreen(navController = navController)
        }

        composable(Routes.EVALUATIONS_HISTORY) {   // ← CORREGIDO AQUÍ
            EvaluationHistoryScreen(navController = navController)
        }

        composable(Routes.BULK_UPLOAD) {
            BulkUploadScreen(onBackClick = { navController.popBackStack() })
        }

        composable(
            route = "${Routes.EVALUATION_DETAIL}/{evaluationId}",
            arguments = listOf(
                navArgument("evaluationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId")!!
            EvaluationDetailScreen(
                evaluationId = evaluationId,
                onBack = { navController.popBackStack() }
            )
        }

        // VACANTES
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

        // DASHBOARD
        composable(Routes.DASHBOARD) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pantalla: Dashboard")
            }
        }

        // MATCHING
        composable(Routes.MATCHING) {
            MatchingScreen(navController)
        }

        // COLABORADOR HOME
        composable(Routes.COLABORADOR_HOME) {
            ColaboradorHomeScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // COLABORADOR SKILLS
        composable(Routes.COLABORADOR_SKILLS) {
            SkillsScreen(navController = navController)
        }

        // SOLICITUDES COLABORADOR
        composable(Routes.SOLICITUDES_COLABORADOR) {
            SolicitudColaboradorScreen(navController = navController)
        }

        // ALERTAS COLABORADOR
        composable(Routes.ALERTAS_COLABORADOR) {
            NotificacionesScreen(navController = navController)
        }
    }
}

@Composable
private fun SimplePlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla: $title")
    }
}
