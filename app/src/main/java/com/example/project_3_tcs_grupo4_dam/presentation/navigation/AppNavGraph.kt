package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.auth.VacantScreen
import com.tuapp.vacantes.NewVacantScreen


object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val VACANT = "vacant"

    const val NEW_VACANCY = "newVacancy"
}

@Composable
fun AppNavGraph(viewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.LOGIN) { LoginScreen(navController, viewModel) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        } }
        composable (Routes.VACANT){VacantScreen(navController) }
        composable (Routes.NEW_VACANCY){ NewVacantScreen(navController) }
    }
}
