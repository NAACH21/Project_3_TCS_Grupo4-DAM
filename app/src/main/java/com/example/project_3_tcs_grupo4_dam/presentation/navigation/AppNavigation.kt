package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen

/**
 * NavHost principal de la aplicación
 * Maneja la navegación entre pantallas según el rol del usuario
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Determinar la ruta inicial
    val startDestination = if (sessionManager.isLoggedIn()) {
        val role = sessionManager.getRol() ?: ""
        AppRoutes.getHomeRouteByRole(role)
    } else {
        AppRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val homeRoute = AppRoutes.getHomeRouteByRole(role)
                    navController.navigate(homeRoute) {
                        // Limpiar el back stack para que no pueda volver al login
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                authViewModel = TODO()
            )
        }

        // Pantalla de Registro (si la implementas después)
        composable(AppRoutes.REGISTER) {
            // TODO: Implementar RegisterScreen
        }

        // Pantalla Home para ADMIN
        composable(AppRoutes.ADMIN_HOME) {
            // TODO: Implementar AdminHomeScreen
            PlaceholderScreen(
                title = "Admin Home",
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Home para BUSINESS_MANAGER
        composable(AppRoutes.MANAGER_HOME) {
            // TODO: Implementar ManagerHomeScreen
            PlaceholderScreen(
                title = "Manager Home",
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Home para COLABORADOR
        composable(AppRoutes.COLABORADOR_HOME) {
            // TODO: Implementar ColaboradorHomeScreen
            PlaceholderScreen(
                title = "Colaborador Home",
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Pantalla placeholder temporal para las home screens
 */
@Composable
private fun PlaceholderScreen(
    title: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}
