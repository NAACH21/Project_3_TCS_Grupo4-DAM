package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudAdminScreen

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEvaluacionesScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Evaluaciones",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                homeRoute = Routes.ADMIN_HOME
            )
        }
    ) { paddingValues ->
        AdminEvaluacionesContent(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun AdminEvaluacionesContent(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Solicitudes", "Evaluaciones")

    Column(modifier = modifier.fillMaxSize()) {
        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = TCSBlue,
            contentColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Contenido condicional (sin navegación, solo cambio de estado)
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> SolicitudAdminScreen(
                    navController = navController,
                    embedded = true,
                    showTopBar = false,
                    showBottomBar = false
                )
                1 -> EvaluationScreen(
                    navController = navController,
                    isStandalone = false
                )
            }
        }
    }
}
