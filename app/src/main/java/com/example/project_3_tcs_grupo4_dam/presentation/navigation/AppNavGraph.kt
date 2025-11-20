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
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorHomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.matching.MatchingScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationsHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.NewVacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.SkillsScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.ColaboradorSkillsScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        composable(Routes.HOME_COLABORADOR) {
            ColaboradorHomeScreen(
                navController = navController,
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // Limpiar toda la pila
                    }
                },
                onNavigateToAlertas = {
                    navController.navigate(Routes.NOTIFICACIONES)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role.equals("COLABORADOR", ignoreCase = true)) {
                        Routes.HOME_COLABORADOR
                    } else {
                        Routes.HOME
                    }

                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // TODO: Navegar a registro si existe
                }
            )
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
            // CORRECCIÓN: Extracción segura del argumento (?: "") evita crash por nulo
            val colaboradorId = backStackEntry.arguments?.getString("colaboradorId") ?: ""
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

        // NUEVA RUTA: Skills del Colaborador
        composable(Routes.COLABORADOR_SKILLS) {
            ColaboradorSkillsScreen(navController = navController)
        }

        composable(Routes.NIVEL_SKILLS) {
            SkillsScreen(navController = navController)
        }

        composable(Routes.NOTIFICACIONES) {
            NotificacionesScreen(navController = navController)
        }

        // RUTAS DE EVALUACIONES
        composable(Routes.EVALUATION_SCREEN) {
            EvaluationScreen(navController = navController)
        }

        composable(Routes.EVALUACIONES) {
            EvaluationsHistoryScreen(
                navController = navController,
                onNavigateToDetail = { id ->
                    navController.navigate("${Routes.EVALUATION_DETAIL}/$id")
                }
            )
        }

        composable(Routes.EVALUATIONS_HISTORY) {
            EvaluationsHistoryScreen(
                navController = navController,
                onNavigateToDetail = { id ->
                    navController.navigate("${Routes.EVALUATION_DETAIL}/$id")
                }
            )
        }

        composable(Routes.BULK_UPLOAD) {
            BulkUploadScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.EVALUATION_DETAIL}/{evaluationId}",
            arguments = listOf(
                navArgument("evaluationId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId")
            SimplePlaceholderScreen(
                title = "Detalle de Evaluación #$evaluationId"
            )
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
