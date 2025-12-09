package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Determinar rol y ruta de inicio correcta
    val rolUsuario = remember { sessionManager.getRol() ?: "ADMIN" }
    val homeRoute = remember(rolUsuario) {
        if (rolUsuario.equals("MANAGER", ignoreCase = true)) Routes.MANAGER_HOME else Routes.ADMIN_HOME
    }

    // Configuración del ViewModel de Notificaciones
    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )

    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()

    // Cargar notificaciones al entrar (Admin/Manager = true)
    LaunchedEffect(Unit) {
        val userId = sessionManager.getColaboradorId()
        // Tratamos como admin si es ADMIN o MANAGER
        val esAdmin = rolUsuario.equals("ADMIN", ignoreCase = true) || rolUsuario.equals("MANAGER", ignoreCase = true)
        notificacionesViewModel.cargarNotificaciones(esAdmin = esAdmin, userId = userId)
    }

    val primaryBlue = Color(0xFF0A63C2)
    val backgroundBlue = Color(0xFF0E4F9C)
    val bgLight = Color(0xFFF6F7FB)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            // FIX: Pasar explícitamente la ruta de inicio según el rol para que el botón "Inicio" funcione bien
            BottomNavBar(navController = navController, homeRoute = homeRoute)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgLight)
        ) {
            // 🔵 HEADER CON GRADIENTE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(primaryBlue, backgroundBlue)
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 26.dp,
                            bottomEnd = 26.dp
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, top = 50.dp) // Bajamos el texto para evitar la status bar
                ) {
                    // Texto dinámico según rol
                    val saludo = if (rolUsuario.equals("MANAGER", ignoreCase = true)) "Hola, Manager 👋" else "Hola, Admin 👋"
                    Text(
                        text = saludo,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bienvenido al sistema de gestión de talento",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Botones de acción en la esquina superior derecha
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 50.dp, end = 12.dp), // Bajamos los botones para evitar la status bar
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notificaciones
                    IconButton(
                        onClick = { navController.navigate(Routes.NOTIFICACIONES_ADMIN) } // FIX: Apuntar a la ruta de admin
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            // Badge Dinámico
                            if (unreadCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp),
                                    containerColor = Color(0xFFFF5252),
                                    contentColor = Color.White
                                ) {
                                    Text(unreadCount.toString(), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Botón de Cerrar Sesión
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Contenido con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // 🔹 TARJETAS COMPACTAS (2x2 grid)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardSmall(
                        title = "Colaboradores",
                        value = "142",
                        subtitle = "Colaboradores activos",
                        iconColor = Color(0xFF245DFF),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCardSmall(
                        title = "Evaluaciones",
                        value = "8",
                        subtitle = "Evaluaciones pendientes",
                        iconColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardSmall(
                        title = "Vacantes",
                        value = "5",
                        subtitle = "Vacantes abiertas",
                        iconColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCardSmall(
                        title = "Cobertura",
                        value = "78%",
                        subtitle = "% Cobertura de skills",
                        iconColor = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 🔸 SECCIÓN "ACCIONES RÁPIDAS"
                Text(
                    text = "Acciones rápidas",
                    modifier = Modifier.padding(horizontal = 2.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botones de acciones rápidas en grid 2x2
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickButton(
                        text = "Matching Inteligente",
                        color = Color(0xFF0A63C2),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.MATCHING) }
                    )
                    QuickButton(
                        text = "Dashboard General",
                        color = Color(0xFF0A63C2),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.DASHBOARD) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickButton(
                        text = "Historial Evaluaciones",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.EVALUACIONES) }
                    )
                    QuickButton(
                        text = "Carga Masiva",
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.BULK_UPLOAD) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickButton(
                        text = "Nueva Evaluación",
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.EVALUATION_SCREEN) }
                    )
                    QuickButton(
                        text = "Nivel Skills",
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.NIVEL_SKILLS) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickButton(
                        text = "Brechas de Skills",
                        color = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SKILLS) }
                    )
                    QuickButton(
                        text = "Solicitudes Entrevista",
                        color = Color(0xFF7B1FA2),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SOLICITUDES_ADMIN) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickButton(
                        text = "Gestión Vacantes",
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.VACANTES) }
                    )
                    QuickButton(
                        text = "Alertas Automáticas",
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 🔹 SECCIÓN "ACTIVIDAD RECIENTE"
                Text(
                    text = "Actividad reciente",
                    modifier = Modifier.padding(horizontal = 2.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lista de actividades recientes
                ActivityCard(
                    title = "Nueva evaluación registrada",
                    subtitle = "Carlos Mendoza - Desarrollador Senior",
                    timeAgo = "2h"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActivityCard(
                    title = "Vacante actualizada",
                    subtitle = "Analista de Datos - Área BI",
                    timeAgo = "5h"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActivityCard(
                    title = "Alerta de brecha crítica",
                    subtitle = "",
                    timeAgo = "1d"
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ======================================================
// COMPONENTES REUTILIZABLES
// ======================================================

@Composable
fun MetricCardSmall(
    @Suppress("UNUSED_PARAMETER") title: String,
    value: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.height(95.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // ICONO MINI
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickButton(
    text: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ActivityCard(
    title: String,
    subtitle: String,
    timeAgo: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = timeAgo,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
