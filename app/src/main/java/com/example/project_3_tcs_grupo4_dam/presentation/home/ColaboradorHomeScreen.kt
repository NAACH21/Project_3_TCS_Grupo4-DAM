package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// Colores definidos según diseño
private val TCSBlue = Color(0xFF00549F)
private val LightGrayBg = Color(0xFFF5F7FA)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)
private val BadgeGreenBg = Color(0xFFE8F5E9)
private val BadgeGreenText = Color(0xFF2E7D32)
private val WarningOrange = Color(0xFFFF9800)

@Composable
fun ColaboradorHomeScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToAlertas: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val colaboradorId = sessionManager.getColaboradorId() ?: ""

    // ViewModel para cargar datos del perfil (nombre, rol, etc.)
    val homeViewModel: ColaboradorHomeViewModel = viewModel(
        factory = ColaboradorHomeViewModelFactory(
            RetrofitClient.colaboradorApi,
            colaboradorId
        )
    )

    val colaborador by homeViewModel.colaborador.collectAsState()

    Scaffold(
        bottomBar = { ColaboradorBottomNavBar(navController) },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. Card de Perfil con datos reales si están disponibles
            if (colaborador != null) {
                ProfileCard(colaborador!!)
            } else {
                // Placeholder loading o skeleton
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TCSBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tu Progreso de Skills
            SkillsProgressCard(
                onSeeAllClick = { navController.navigate(Routes.COLABORADOR_SKILLS) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Requerimientos Urgentes
            UrgentRequirementsCard()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de cerrar sesión
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun ProfileCard(colaborador: ColaboradorReadDto) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(id = R.drawable.auth_logo_tata), // Placeholder image
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column {
                Text(
                    text = "Hola, ${colaborador.nombres} ${colaborador.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = colaborador.rolActual,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // CORRECCIÓN: Manejo de valor nulo para area usando operador elvis
                    Badge(text = colaborador.area ?: "Sin área", colorBg = Color(0xFFE3F2FD), colorText = TCSBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(text = "Activo", colorBg = BadgeGreenBg, colorText = BadgeGreenText)
                }
                
                // Correo (si estuviera en el DTO, por ahora hardcodeado o derivado)
                // Text(text = colaborador.correo, ...)
            }
        }
    }
}

@Composable
fun Badge(text: String, colorBg: Color, colorText: Color) {
    Surface(
        color = colorBg,
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colorText
        )
    }
}

@Composable
fun SkillsProgressCard(onSeeAllClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = TCSBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tu Progreso de Skills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progreso General", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("80%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.8f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TCSBlue,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Grid de Estadísticas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatItem(count = "6", label = "Avanzado", colorBg = BadgeGreenBg, modifier = Modifier.weight(1f))
                StatItem(count = "2", label = "Intermedio", colorBg = Color(0xFFE3F2FD), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatItem(count = "0", label = "Básico", colorBg = Color(0xFFFFF3E0), modifier = Modifier.weight(1f))
                StatItem(count = "0", label = "No iniciado", colorBg = LightGrayBg, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSeeAllClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TCSBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver todos mis skills", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String, colorBg: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(colorBg, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
fun UrgentRequirementsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Requerimientos Urgentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tienes 2 skills que requieren actualización",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Item de Skill Pendiente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Node.js", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Estado: Pendiente", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Surface(
                    color = Color(0xFFFFECB3),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Pendiente",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF57F17),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ColaboradorBottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Inicio", Icons.Default.Home, Routes.HOME_COLABORADOR),
            BottomNavItem("Skills", Icons.Default.TrendingUp, Routes.COLABORADOR_SKILLS),
            BottomNavItem("Vacantes", Icons.Default.Work, Routes.VACANTES_COLABORADOR),
            BottomNavItem("Notificaciones", Icons.Outlined.Notifications, Routes.NOTIFICACIONES)
        )
        
        // Obtener ruta actual para resaltar el item seleccionado (lógica simplificada)
        val currentRoute = navController.currentDestination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME_COLABORADOR) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TCSBlue,
                    selectedTextColor = TCSBlue,
                    indicatorColor = TCSBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
