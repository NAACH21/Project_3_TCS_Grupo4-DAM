package com.example.project_3_tcs_grupo4_dam.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradoresScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationsHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorHomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.ActualizarSkillScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.ColaboradorSkillsScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.NewVacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantesColaboradorScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Función para cerrar sesión completamente
    fun performLogout() {
        sessionManager.clearSession()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true } // Limpia TODO el backstack
        }
    }

    // Lógica inicial: Verificar sesión
    LaunchedEffect(Unit) {
        val role = sessionManager.getRol()
        val logged = sessionManager.isLoggedIn()
        
        Log.d("AppNavigation", "Check Sesión -> Logged: $logged, Role: $role")

        if (logged && !role.isNullOrEmpty()) {
            val destination = when (role.uppercase()) {
                "COLABORADOR" -> Routes.COLABORADOR_HOME
                "ADMIN" -> Routes.ADMIN_HOME
                "MANAGER" -> Routes.MANAGER_HOME
                else -> Routes.LOGIN
            }
            navController.navigate(destination) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // ---------------- LOGIN ----------------
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role.uppercase()) {
                        "COLABORADOR" -> Routes.COLABORADOR_HOME
                        "ADMIN" -> Routes.ADMIN_HOME
                        "MANAGER" -> Routes.MANAGER_HOME
                        else -> Routes.LOGIN
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            PlaceholderScreen("Registro") { navController.popBackStack() }
        }

        // ---------------- HOMES POR ROL ----------------
        
        // 1. HOME COLABORADOR
        composable(Routes.COLABORADOR_HOME) {
            ColaboradorHomeScreen(
                navController = navController,
                onLogout = { performLogout() },
                onNavigateToAlertas = { navController.navigate(Routes.ALERTAS_COLABORADOR) }
            )
        }

        // 2. HOME ADMIN
        composable(Routes.ADMIN_HOME) {
            // Asumo que existe AdminHomeScreen o usamos HomeScreen genérico
            // Si usas HomeScreen, asegúrate de que reciba onLogout
            HomeScreen(
                navController = navController,
                onLogout = { performLogout() }
            )
        }
        
        // 3. HOME MANAGER
        composable(Routes.MANAGER_HOME) {
            HomeScreen(
                navController = navController,
                onLogout = { performLogout() }
            )
        }

        // ---------------- VACANTES (SEPARADAS) ----------------

        // Vista Admin / Manager
        composable(Routes.VACANTES_ADMIN) {
            VacantScreen(navController = navController)
        }
        
        // Vista Colaborador (Job Match)
        composable(Routes.VACANTES_COLABORADOR) {
            VacantesColaboradorScreen(navController = navController)
        }

        // Crear Vacante (Admin)
        composable("newVacancy") { // Mantenemos la ruta string si NewVacantScreen la usa hardcoded, o idealmente usar Routes.NEW_VACANT
            NewVacantScreen(navController = navController) { navController.popBackStack() }
        }
        composable(Routes.NEW_VACANT) {
            NewVacantScreen(navController = navController) { navController.popBackStack() }
        }

        // ---------------- GESTIÓN ADMIN ----------------
        
        composable(Routes.COLABORADORES) { ColaboradoresScreen(navController = navController) }
        
        composable(Routes.EVALUACIONES_ADMIN) { 
            EvaluationsHistoryScreen(
                navController = navController,
                onNavigateToDetail = { id -> navController.navigate(Routes.evaluationDetail(id)) }
            )
        }
        
        // Alias para compatibilidad
        composable("evaluaciones") { // Por si algún botón usa string hardcoded
             EvaluationsHistoryScreen(
                navController = navController,
                onNavigateToDetail = { id -> navController.navigate(Routes.evaluationDetail(id)) }
            )
        }

        composable(Routes.NUEVA_EVALUACION) { EvaluationScreen(navController = navController) }
        composable(Routes.EVALUACIONES_ADMIN) { EvaluationScreen(navController = navController) } // Alias

        composable(Routes.BULK_UPLOAD) { 
            BulkUploadScreen(onBackClick = { navController.popBackStack() }) 
        }

        composable(Routes.DASHBOARD_ADMIN) { PlaceholderScreen("Dashboard General") { navController.popBackStack() } }
        composable(Routes.MATCHING) { PlaceholderScreen("Matching Inteligente") { navController.popBackStack() } }
        
        composable(Routes.ALERTAS_ADMIN) { NotificacionesScreen(navController = navController) }


        // ---------------- GESTIÓN COLABORADOR ----------------
        
        composable(Routes.COLABORADOR_SKILLS) { ColaboradorSkillsScreen(navController = navController) }
        
        composable(Routes.ALERTAS_COLABORADOR) { NotificacionesScreen(navController = navController) }
        composable(Routes.ALERTAS_COLABORADOR) { NotificacionesScreen(navController = navController) } // Alias

        // RUTA ACTUALIZAR SKILL (CORREGIDA)
        composable(
            route = "${Routes.ACTUALIZAR_SKILL_BASE}/{id}/{skill}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("skill") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val colaboradorId = backStackEntry.arguments?.getString("id") ?: ""
            val skillName = backStackEntry.arguments?.getString("skill") ?: ""
            ActualizarSkillScreen(navController, colaboradorId, skillName)
        }

        // ---------------- PLACEHOLDERS Y DETALLES ----------------

        composable("${Routes.EVALUATION_DETAIL}/{id}") { backStackEntry ->
             val id = backStackEntry.arguments?.getString("id") ?: ""
             PlaceholderScreen("Detalle Evaluación: $id") { navController.popBackStack() }
        }
        
        composable(Routes.NIVEL_SKILLS) { PlaceholderScreen("Niveles de Skills") { navController.popBackStack() } }
        composable(Routes.SKILLS_ADMIN) { PlaceholderScreen("Brechas de Skills") { navController.popBackStack() } }
        
        // Alias de compatibilidad para "skills" (Admin)
        composable("skills") { PlaceholderScreen("Brechas de Skills") { navController.popBackStack() } }

        // Alias Dashboard
        composable("dashboard") { PlaceholderScreen("Dashboard") { navController.popBackStack() } }
    }
}

@Composable
fun PlaceholderScreen(title: String, onBackOrLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackOrLogout) { Text("Volver") }
    }
}
