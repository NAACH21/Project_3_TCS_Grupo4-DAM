package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorDetalleScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorFormScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradoresScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.NuevoColaboradorScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val COLABORADORES = "colaboradores"
    const val COLABORADOR_DETALLE = "colaborador_detalle"
    const val NUEVO_COLABORADOR = "nuevo_colaborador"
    const val COLABORADOR_FORM = "colaborador_form"
}

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            // ⬅️ pasar navController a HomeScreen
            HomeScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(viewModel) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }

        composable(Routes.COLABORADORES) {
            ColaboradoresScreen(navController)
        }

        // Ruta para el detalle del colaborador
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

        // Ruta antigua para crear colaborador (mantener por compatibilidad)
        composable(Routes.NUEVO_COLABORADOR) {
            NuevoColaboradorScreen(navController)
        }

        // Nueva ruta para formulario reutilizable (crear)
        composable(Routes.COLABORADOR_FORM) {
            ColaboradorFormScreen(navController)
        }

        // Nueva ruta para formulario reutilizable (editar)
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
    }
}
