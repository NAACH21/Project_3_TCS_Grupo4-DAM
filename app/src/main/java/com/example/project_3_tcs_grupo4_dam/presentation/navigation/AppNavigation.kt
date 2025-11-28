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
import androidx.compose.runtime.remember
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
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.auth.LoginScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradorDetalleScreen
import com.example.project_3_tcs_grupo4_dam.presentation.colaborador.ColaboradoresScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.AdminEvaluacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.BulkUploadScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationScreen
import com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones.EvaluationsHistoryScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorHomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.home.HomeScreen
import com.example.project_3_tcs_grupo4_dam.presentation.matching.MatchingScreen
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.ActualizarSkillScreen
import com.example.project_3_tcs_grupo4_dam.presentation.skills.ColaboradorSkillsScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudActualizacionSkillsScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudCertificacionScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudColaboradorScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudColaboradorViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.SolicitudAdminScreen
import com.example.project_3_tcs_grupo4_dam.presentation.solicitud.NuevaEntrevistaScreen
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepositoryImpl
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.NewVacantScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantesColaboradorScreen
import com.example.project_3_tcs_grupo4_dam.presentation.vacantes.VacantScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    fun performLogout() {
        sessionManager.clearSession()
        // Limpiar token de Retrofit
        RetrofitClient.clearToken()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    LaunchedEffect(Unit) {
        val role = sessionManager.getRol()
        val logged = sessionManager.isLoggedIn()
        
        // --- NUEVO: Recuperar token si hay sesión activa ---
        if (logged) {
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                RetrofitClient.setJwtToken(token)
                Log.d("AppNavigation", "Sesión recuperada. Token JWT configurado.")
            }
        }
        
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
        // --- LOGIN ---
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
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) { PlaceholderScreen("Registro") { navController.popBackStack() } }

        // --- HOMES ---
        composable(Routes.COLABORADOR_HOME) {
            ColaboradorHomeScreen(
                navController = navController,
                onLogout = { performLogout() },
                onNavigateToAlertas = { navController.navigate(Routes.ALERTAS_COLABORADOR) }
            )
        }
        composable(Routes.ADMIN_HOME) {
            HomeScreen(navController = navController, onLogout = { performLogout() })
        }
        composable(Routes.MANAGER_HOME) {
            HomeScreen(navController = navController, onLogout = { performLogout() })
        }

        // --- VACANTES ---
        composable(Routes.VACANTES_ADMIN) { VacantScreen(navController = navController) }
        composable(Routes.VACANTES_COLABORADOR) { VacantesColaboradorScreen(navController = navController) }
        composable(Routes.NEW_VACANT) { NewVacantScreen(navController = navController, onBack = { navController.popBackStack() }) }
        composable("newVacancy") { NewVacantScreen(navController = navController, onBack = { navController.popBackStack() }) }

        // --- GESTIÓN ADMIN ---
        composable(Routes.COLABORADORES) { ColaboradoresScreen(navController = navController) }
        
        composable(
            route = "${Routes.COLABORADOR_DETALLE}/{colaboradorId}",
            arguments = listOf(
                navArgument("colaboradorId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val colaboradorId = backStackEntry.arguments?.getString("colaboradorId") ?: ""
            ColaboradorDetalleScreen(
                colaboradorId = colaboradorId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.EVALUACIONES_ADMIN) { 
            AdminEvaluacionesScreen(navController = navController)
        }
        composable(Routes.NUEVA_EVALUACION) { EvaluationScreen(navController = navController) }
        composable(Routes.EVALUATION_SCREEN) { EvaluationScreen(navController = navController) }
        composable(Routes.BULK_UPLOAD) { BulkUploadScreen(onBackClick = { navController.popBackStack() }) }
        
        composable(Routes.NOTIFICACIONES) { NotificacionesScreen(navController = navController) }
        
        composable(Routes.ALERTAS_ADMIN) { NotificacionesScreen(navController = navController) }
        composable(Routes.ALERTAS_COLABORADOR) { NotificacionesScreen(navController = navController) }
        composable(Routes.DASHBOARD_ADMIN) { PlaceholderScreen("Dashboard General") { navController.popBackStack() } }
        
        composable(Routes.MATCHING) { 
            MatchingScreen(navController = navController) 
        }

        // --- GESTIÓN COLABORADOR ---
        composable(Routes.COLABORADOR_SKILLS) { ColaboradorSkillsScreen(navController = navController) }

        // Ruta de solicitudes del colaborador (pantalla principal)
        composable(Routes.SOLICITUDES_COLABORADOR) {
            SolicitudColaboradorScreen(navController = navController)
        }

        // --- SOLICITUDES COLABORADOR (Pantallas completas) ---
        composable(Routes.SOLICITUD_CERTIFICACION_COLABORADOR) {
            val viewModel: SolicitudColaboradorViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return SolicitudColaboradorViewModel(
                            solicitudesRepository = SolicitudesRepositoryImpl(RetrofitClient.solicitudesApi),
                            catalogoRepository = CatalogoRepositoryImpl(),
                            sessionManager = sessionManager
                        ) as T
                    }
                }
            )
            SolicitudCertificacionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Routes.SOLICITUD_SKILLS_COLABORADOR) {
            val viewModel: SolicitudColaboradorViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return SolicitudColaboradorViewModel(
                            solicitudesRepository = SolicitudesRepositoryImpl(RetrofitClient.solicitudesApi),
                            catalogoRepository = CatalogoRepositoryImpl(),
                            sessionManager = sessionManager
                        ) as T
                    }
                }
            )
            SolicitudActualizacionSkillsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // --- SOLICITUDES ADMIN (Entrevistas de Desempeño) ---
        composable(Routes.SOLICITUDES_ADMIN) {
            SolicitudAdminScreen(
                navController = navController,
                embedded = false
            )
        }

        composable(Routes.NUEVA_ENTREVISTA_ADMIN) {
            NuevaEntrevistaScreen(navController = navController)
        }

        composable(
            route = "${Routes.ACTUALIZAR_SKILL_BASE}/{id}/{skill}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }, navArgument("skill") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: ""
            val skill = back.arguments?.getString("skill") ?: ""
            ActualizarSkillScreen(navController, id, skill)
        }

        // --- OTROS ---
        composable("${Routes.EVALUATION_DETAIL}/{id}") { PlaceholderScreen("Detalle Evaluación") { navController.popBackStack() } }
        composable(Routes.NIVEL_SKILLS) { PlaceholderScreen("Niveles de Skills") { navController.popBackStack() } }
        composable(Routes.SKILLS_ADMIN) { PlaceholderScreen("Brechas de Skills") { navController.popBackStack() } }
        composable("skills") { PlaceholderScreen("Brechas de Skills") { navController.popBackStack() } }
        composable("dashboard") { PlaceholderScreen("Dashboard") { navController.popBackStack() } }
    }
}

@Composable
fun PlaceholderScreen(title: String, onBackOrLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackOrLogout) { Text("Volver") }
    }
}
