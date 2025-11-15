package com.example.project_3_tcs_grupo4_dam.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavGraph
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import com.example.project_3_tcs_grupo4_dam.ui.theme.Project_3_TCS_Grupo4DAMTheme

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
            Project_3_TCS_Grupo4DAMTheme {
                // 4. Usar la factory para obtener el ViewModel
                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                // Siempre iniciar en LOGIN
                AppNavGraph(viewModel = authViewModel, startDestination = Routes.LOGIN)
            }
        }
    }
}
