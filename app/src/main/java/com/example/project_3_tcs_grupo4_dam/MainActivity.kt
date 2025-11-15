package com.example.project_3_tcs_grupo4_dam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
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
        enableEdgeToEdge()

        // Inicializar el repositorio y ViewModel
        val repository = AuthRepositoryImpl(RetrofitClient.api, applicationContext)
        val factory = AuthViewModelFactory(repository)
        val authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Determinar pantalla inicial (login si no hay token, home si hay token)
        val startDestination = if (authViewModel.token() != null) {
            Routes.HOME
        } else {
            Routes.LOGIN
        }

        setContent {
            Project_3_TCS_Grupo4DAMTheme(darkTheme = true, dynamicColor = false) {
                AppNavGraph(
                    viewModel = authViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}