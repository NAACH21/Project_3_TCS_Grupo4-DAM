package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesViewModel

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
    @Suppress("UNUSED_PARAMETER") onNavigateToAlertas: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val colaboradorId = sessionManager.getColaboradorId() ?: ""

    // ViewModel para cargar datos del perfil
    val homeViewModel: ColaboradorHomeViewModel = viewModel(
        factory = ColaboradorHomeViewModelFactory(
            RetrofitClient.colaboradorApi,
            colaboradorId
        )
    )

    // ViewModel para las notificaciones (Badge)
    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )
    
    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()
    val colaborador by homeViewModel.colaborador.collectAsState()

    Scaffold(
        // FIX: Pasamos el contador de no leídos (Int) al bottom bar
        bottomBar = { ColaboradorBottomNavBar(navController, unreadCount) },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. Card de Perfil
            colaborador?.let { colab ->
                ProfileCard(colab)
            } ?: Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TCSBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Progreso de Skills
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
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
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
            Image(
                painter = painterResource(id = R.drawable.auth_logo_tata),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Hola, ${colaborador.nombres} ${colaborador.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = colaborador.rolLaboral,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    colaborador.area?.let { area ->
                        Badge(text = area, colorBg = Color(0xFFE3F2FD), colorText = TCSBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Badge(
                        text = if (colaborador.estado == "ACTIVO") "Activo" else "Inactivo",
                        colorBg = if (colaborador.estado == "ACTIVO") BadgeGreenBg else Color(0xFFFFEBEE),
                        colorText = if (colaborador.estado == "ACTIVO") BadgeGreenText else Color(0xFFC62828)
                    )
                }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progreso General", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("80%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TCSBlue,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            // Aquí iría el contenido de los requerimientos urgentes

        }
    }
}