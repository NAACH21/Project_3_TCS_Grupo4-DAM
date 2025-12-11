package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

// Colores Institucionales Modernos
private val PrimaryBlue = Color(0xFF00549F)
private val DarkBlue = Color(0xFF003870)
private val BackgroundColor = Color(0xFFF4F6F9)
private val CardWhite = Color.White
private val TextDark = Color(0xFF1E293B)
private val TextLight = Color(0xFF64748B)

// Colores de Acento (Pastel/Suaves)
private val AccentBlue = Color(0xFFE0F2FE)
private val AccentGreen = Color(0xFFDCFCE7)
private val AccentOrange = Color(0xFFFFF7ED)
private val AccentPurple = Color(0xFFF3E8FF)

@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    val rolUsuario = remember { sessionManager.getRol() ?: "ADMIN" }
    val homeRoute = remember(rolUsuario) {
        if (rolUsuario.equals("MANAGER", ignoreCase = true)) Routes.MANAGER_HOME else Routes.ADMIN_HOME
    }

    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )

    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        val userId = sessionManager.getColaboradorId()
        val esAdmin = rolUsuario.equals("ADMIN", ignoreCase = true) || rolUsuario.equals("MANAGER", ignoreCase = true)
        notificacionesViewModel.cargarNotificaciones(esAdmin = esAdmin, userId = userId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController, homeRoute = homeRoute)
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. HEADER MODERNO
            HeaderAdmin(rolUsuario, unreadCount, navController, onLogout)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                
                Spacer(modifier = Modifier.height(24.dp))

                // 2. DASHBOARD METRICS (KPIs)
                Text(
                    "Resumen General",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCardModern(
                        title = "Colaboradores",
                        value = "142",
                        trend = "+12%",
                        icon = Icons.Default.Group,
                        colorBg = AccentBlue,
                        colorIcon = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCardModern(
                        title = "Evaluaciones",
                        value = "8",
                        trend = "Pendientes",
                        icon = Icons.Default.Assignment,
                        colorBg = AccentOrange,
                        colorIcon = Color(0xFFEA580C),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCardModern(
                        title = "Vacantes",
                        value = "5",
                        trend = "Activas",
                        icon = Icons.Default.Work,
                        colorBg = AccentGreen,
                        colorIcon = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCardModern(
                        title = "Cobertura",
                        value = "78%",
                        trend = "Skills",
                        icon = Icons.Default.PieChart,
                        colorBg = AccentPurple,
                        colorIcon = Color(0xFF9333EA),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. MENÚ DE GESTIÓN (GRID)
                Text(
                    "Gestión y Herramientas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Fila 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionButton(
                        text = "Matching",
                        icon = Icons.Default.Bolt,
                        color = PrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.MATCHING) }
                    )
                    ActionButton(
                        text = "Dashboard",
                        icon = Icons.Default.BarChart,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.DASHBOARD) }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Fila 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionButton(
                        text = "Historial",
                        icon = Icons.Default.History,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.EVALUACIONES) }
                    )
                    ActionButton(
                        text = "Carga Masiva",
                        icon = Icons.Default.UploadFile,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.BULK_UPLOAD) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionButton(
                        text = "Nueva Eval.",
                        icon = Icons.Default.AddTask,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.EVALUATION_SCREEN) }
                    )
                    ActionButton(
                        text = "Skills Gap",
                        icon = Icons.Default.TrendingDown,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SKILLS) }
                    )
                }
                
                 Spacer(modifier = Modifier.height(12.dp))

                // Fila 4
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                     ActionButton(
                        text = "Solicitudes",
                        icon = Icons.Default.FolderShared,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SOLICITUDES_ADMIN) }
                    )
                    ActionButton(
                        text = "Vacantes",
                        icon = Icons.Default.BusinessCenter,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.VACANTES) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. ACTIVIDAD RECIENTE
                Text(
                    "Actividad Reciente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ActivityItem(
                    title = "Nueva evaluación completada",
                    subtitle = "Carlos Mendoza • Desarrollo",
                    time = "Hace 2h",
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF16A34A)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                ActivityItem(
                    title = "Vacante Senior Java publicada",
                    subtitle = "RRHH • Tecnología",
                    time = "Hace 5h",
                    icon = Icons.Default.Campaign,
                    iconColor = PrimaryBlue
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ==========================================
// COMPONENTES DE DISEÑO
// ==========================================

@Composable
fun HeaderAdmin(rol: String, unreadCount: Int, navController: NavController, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, DarkBlue)
                )
            )
    ) {
        // Patrón de fondo decorativo (círculos sutiles)
        Box(
            modifier = Modifier
                .offset(x = 280.dp, y = (-50).dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = 80.dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Perfil / Saludo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = rol.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (rol.equals("MANAGER", true)) "Hola, Manager" else "Hola, Admin",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bienvenido de nuevo",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Botones
                Row {
                    IconButton(onClick = { navController.navigate(Routes.NOTIFICACIONES_ADMIN) }) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFFF5252),
                                        contentColor = Color.White
                                    ) { Text("$unreadCount") }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Alertas",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Salir",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCardModern(
    title: String,
    value: String,
    trend: String,
    icon: ImageVector,
    colorBg: Color,
    colorIcon: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = colorIcon, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Valor y Título
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextLight
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tendencia
            Surface(
                color = BackgroundColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = trend,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        modifier = modifier.height(85.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if(text == "Matching") PrimaryBlue else color, // Resaltar matching
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ActivityItem(
    title: String,
    subtitle: String,
    time: String,
    icon: ImageVector,
    iconColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLight
                )
            }
            
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = TextLight
            )
        }
    }
}
