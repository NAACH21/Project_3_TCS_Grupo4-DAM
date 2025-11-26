package com.example.project_3_tcs_grupo4_dam.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavGraph
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtener AuthApiService desde RetrofitClient
        val authApiService = RetrofitClient.authApi

        // 2. Crear instancia del repositorio con sus dependencias
        val authRepository = AuthRepositoryImpl(authApiService, applicationContext)

        // 3. Crear la factory para el ViewModel
        val authViewModelFactory = AuthViewModelFactory(authRepository)

        setContent {
            MaterialTheme {
                // Usar wrapper composable para instanciar el ViewModel con la factory
                RootApp(authViewModelFactory = authViewModelFactory, startDestination = Routes.HomeScreen.route)
            }
        }
    }
}

@Composable
private fun RootApp(authViewModelFactory: AuthViewModelFactory, startDestination: String) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    AppNavGraph(viewModel = authViewModel, startDestination = startDestination)
}
