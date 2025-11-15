package com.example.project_3_tcs_grupo4_dam.ui.colaborador.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.ui.colaborador.screens.*

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Home) { HomeScreen() }
            composable(Routes.Skills) { SkillsScreen() }
            composable(Routes.Actualizar) { ActualizarScreen() }
            composable(Routes.Vacantes) { VacantesScreen() }
            composable(Routes.Notificaciones) { NotificacionesScreen() }
        }
    }
}
