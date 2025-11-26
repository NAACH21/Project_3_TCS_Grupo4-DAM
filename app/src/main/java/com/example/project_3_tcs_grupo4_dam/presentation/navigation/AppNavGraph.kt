package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorDetalleScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorFormScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorListScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationDetailScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.main.MainScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LoginScreen.route) {
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                onLoggedIn = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(Routes.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(Routes.ColaboradorListScreen.route) {
            ColaboradorListScreen(navController = navController)
        }
        composable(Routes.ColaboradorDetailScreen.route) {
            val colaboradorId = it.arguments?.getString("colaboradorId") ?: ""
            ColaboradorDetalleScreen(colaboradorId = colaboradorId) {
                navController.popBackStack()
            }
        }
        composable(Routes.ColaboradorFormScreen.route) {
            ColaboradorFormScreen(navController = navController) // TODO: Add onSave and onCancel
        }
        composable(Routes.EvaluationScreen.route) {
            EvaluationScreen(navController = navController)
        }
        composable(Routes.EvaluationHistoryScreen.route) {
            EvaluationHistoryScreen(navController = navController)
        }
        composable(Routes.EvaluationDetailScreen.route) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId") ?: ""
            EvaluationDetailScreen(evaluationId = evaluationId, onBack = {
                navController.popBackStack()
            })
        }
        composable(Routes.BulkUploadScreen.route) {
            BulkUploadScreen(onBackClick = { navController.popBackStack() })
        }
    }
}