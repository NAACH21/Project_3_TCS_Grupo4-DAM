package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorHomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen

/**
 * NavHost principal de la aplicación
 * Maneja la navegación entre pantallas según el rol del usuario
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Solo una instancia del SessionManager
    val sessionManager = remember { SessionManager(context) }

    // Estados que se actualizarán automáticamente
    var currentRole by remember { mutableStateOf(sessionManager.getRol()) }
    var isLogged by remember { mutableStateOf(sessionManager.isLoggedIn()) }

    // Determinar pantalla inicial
    val startDestination =
        if (isLogged && !currentRole.isNullOrEmpty()) {
            AppRoutes.getHomeRouteByRole(currentRole!!)
        } else {
            AppRoutes.LOGIN
        }

    // Esto refresca cada recomposición importante
    LaunchedEffect(Unit) {
        Log.d("DEBUG_NAV", "SessionManager.isLoggedIn() = ${sessionManager.isLoggedIn()}")
        Log.d("DEBUG_NAV", "SessionManager.getRol() = '${sessionManager.getRol()}'")
        Log.d("DEBUG_NAV", "Start destination = $startDestination")
        currentRole = sessionManager.getRol()
        isLogged = sessionManager.isLoggedIn()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- LOGIN ----------------
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val homeRoute = AppRoutes.getHomeRouteByRole(role)

                    // IMPORTANTÍSIMO: ACTUALIZAMOS ESTADO LOCAL
                    currentRole = role
                    isLogged = true

                    navController.navigate(homeRoute) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        // ---------------- REGISTER ----------------
        composable(AppRoutes.REGISTER) {
            // TODO
        }

        // ---------------- ADMIN ----------------
        composable(AppRoutes.ADMIN_HOME) {
            PlaceholderScreen(
                title = "Admin Home",
                onLogout = {
                    sessionManager.clearSession()
                    currentRole = null
                    isLogged = false

                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- MANAGER ----------------
        composable(AppRoutes.MANAGER_HOME) {
            PlaceholderScreen(
                title = "Manager Home",
                onLogout = {
                    sessionManager.clearSession()
                    currentRole = null
                    isLogged = false

                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- COLABORADOR HOME ----------------
        composable(AppRoutes.COLABORADOR_HOME) {
            ColaboradorHomeScreen(
                navController = navController,
                onLogout = {
                    sessionManager.clearSession()
                    currentRole = null
                    isLogged = false

                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAlertas = {
                    navController.navigate(AppRoutes.COLABORADOR_ALERTAS)
                }
            )
        }

        // ---------------- COLABORADOR ALERTAS ----------------
        composable(AppRoutes.COLABORADOR_ALERTAS) {
            NotificacionesScreen(navController = navController)
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text(text = "Cerrar Sesión")
        }
    }
}