package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationsHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, viewModel) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
        composable(Routes.HOME) { HomeScreen() } // Add navigation to EvaluationScreen from here if needed

        composable(Routes.EVALUATION_SCREEN) {
            EvaluationScreen(
                onBackClick = { navController.popBackStack() },
                onHistoryClick = { navController.navigate(Routes.EVALUATIONS_HISTORY_SCREEN) },
                onBulkLoadClick = { navController.navigate(Routes.BULK_UPLOAD_SCREEN) }
            )
        }

        composable(Routes.BULK_UPLOAD_SCREEN) {
            BulkUploadScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.EVALUATIONS_HISTORY_SCREEN) {
            EvaluationsHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Routes.evaluationDetail(id)) }
            )
        }

        composable(Routes.EVALUATION_DETAIL_SCREEN) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("id")
            // You would create this screen to show details
            // EvaluationDetailScreen(evaluationId = evaluationId, onBackClick = { navController.popBackStack() })
        }
    }
}
