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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

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

    // Determinar la ruta inicial asegurando que exista token y rol válidos
    val storedToken = sessionManager.getToken()
    val storedRole = sessionManager.getRol()

    val startDestination = if (!storedToken.isNullOrBlank() && !storedRole.isNullOrBlank()) {
        // Si hay sesión previa, configurar el token en Retrofit para no perder la autenticación
        RetrofitClient.setJwtToken(storedToken)
        when (storedRole.uppercase()) {
            "ADMIN" -> Routes.ADMIN_HOME
            "BUSINESS_MANAGER" -> Routes.MANAGER_HOME
            "COLABORADOR" -> Routes.COLABORADOR_HOME
            else -> Routes.LOGIN
        }
    } else {
        Routes.LOGIN
    }

    val authRepository = AuthRepositoryImpl(RetrofitClient.authApi, sessionManager)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))

    AppNavGraph(viewModel = authViewModel, startDestination = startDestination)
}
