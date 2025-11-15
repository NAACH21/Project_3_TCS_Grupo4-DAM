package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
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

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val COLABORADOR_FORM = "colaborador_form"
    const val MATCHING = "matching"

    // Nuevas rutas para la barra inferior
    const val SKILLS = "skills"
    const val EVALUACIONES = "evaluaciones"
    const val VACANTES = "vacantes"
    const val DASHBOARD = "dashboard"
}

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

        composable(Routes.LOGIN) {
            LoginScreen(navController, viewModel) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
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

        // Rutas adicionales (placeholders)
        composable(Routes.SKILLS) {
            SimplePlaceholderScreen(title = "Skills", navController = navController)
        }

        composable(Routes.EVALUACIONES) {
            SimplePlaceholderScreen(title = "Evaluaciones", navController = navController)
        }

        composable(Routes.VACANTES) {
            SimplePlaceholderScreen(title = "Vacantes", navController = navController)
        }

        composable(Routes.DASHBOARD) {
            SimplePlaceholderScreen(title = "Dashboard", navController = navController)
        }

        composable(Routes.MATCHING) {
            MatchingScreen(navController)
        }
    }
}

@Composable
private fun SimplePlaceholderScreen(title: String, navController: androidx.navigation.NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $title")
    }
}
