package com.example.project_3_tcs_grupo4_dam.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavGraph
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavigation
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtener AuthApiService desde RetrofitClient
        val authApiService = RetrofitClient.authApi

        // 2. Crear SessionManager
        val sessionManager = SessionManager(applicationContext)

        // 3. Crear instancia del repositorio con sus dependencias
        val authRepository = AuthRepositoryImpl(authApiService, sessionManager)

        // 4. Crear la factory para el ViewModel
        val authViewModelFactory = AuthViewModelFactory(authRepository)
// 🔥 LIMPIAR SESIÓN AL INICIAR
        sessionManager.clearSession()

        setContent {
            MaterialTheme {
                // Usar wrapper composable para instanciar el ViewModel con la factory
                RootApp(authViewModelFactory = authViewModelFactory)
            }
        }
    }
}

@Composable
private fun RootApp(authViewModelFactory: AuthViewModelFactory) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    AppNavigation()

}
