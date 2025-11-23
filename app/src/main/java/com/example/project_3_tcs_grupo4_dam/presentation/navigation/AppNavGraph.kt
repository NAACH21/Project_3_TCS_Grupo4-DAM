package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen

// ESTE ARCHIVO PARECE SER REDUNDANTE CON AppNavigation.kt
// Se recomienda usar AppNavigation.kt como entrada principal.
// Si MainActivity llama a este AppNavGraph, debería migrarse.

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
        
        // NOTA: Si este archivo se usa, se deberían copiar TODAS las definiciones de AppNavigation.kt
        // Para evitar duplicidad y errores, recomiendo usar AppNavigation.kt en MainActivity
    }
}
