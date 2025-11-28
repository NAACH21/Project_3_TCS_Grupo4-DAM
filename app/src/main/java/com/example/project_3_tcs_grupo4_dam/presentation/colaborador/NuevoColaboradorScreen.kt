package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun NuevoColaboradorScreen(navController: NavController) {
    // Reutilizamos la pantalla de formulario (modo crear: sin colaboradorId en SavedState)
    ColaboradorFormScreen(navController = navController)
}
