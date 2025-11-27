package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory

/**
 * NavHost principal de la aplicación
 * Maneja la navegación entre pantallas según el rol del usuario
 */
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Determinar la ruta inicial asegurando que exista token y rol válidos
    val storedToken = sessionManager.getToken()

    // Siempre iniciar en login. Si hay token guardado, lo configuramos para las llamadas API,
    // pero no saltamos la pantalla de login automáticamente.
    if (!storedToken.isNullOrBlank()) {
        RetrofitClient.setJwtToken(storedToken)
    }

    val authRepository = AuthRepositoryImpl(RetrofitClient.authApi, sessionManager)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))

    // Forzamos que la navegación comience en el login (AppNavGraph usa Routes.LOGIN como startDestination)
    AppNavGraph(viewModel = authViewModel)
}