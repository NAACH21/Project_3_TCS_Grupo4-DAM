package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesViewModel
import kotlin.math.min

// Colores definidos según diseño
private val TCSBlue = Color(0xFF00549F)
private val TCSLightBlue = Color(0xFF0D47A1)
private val LightGrayBg = Color(0xFFF5F7FA)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)
private val BadgeGreenBg = Color(0xFFE8F5E9)
private val BadgeGreenText = Color(0xFF2E7D32)
private val WarningOrange = Color(0xFFFF9800)
private val CardWhite = Color.White

@Composable
fun ColaboradorHomeScreen(
    navController: NavController,
    onLogout: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onNavigateToAlertas: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val colaboradorId = remember { sessionManager.getColaboradorId() ?: "" }

    // ViewModel para cargar datos del perfil
    val homeViewModel: ColaboradorHomeViewModel = viewModel(
        factory = ColaboradorHomeViewModelFactory(
            RetrofitClient.colaboradorApi,
            colaboradorId
        )
    )

    // ViewModel para las notificaciones
    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )
    
    // Cargar notificaciones
    LaunchedEffect(Unit) {
        notificacionesViewModel.cargarAlertas()
    }
    
    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()
    val alertasUi by notificacionesViewModel.alertasUi.collectAsState()
    val colaborador by homeViewModel.colaborador.collectAsState()

    Scaffold(
        bottomBar = { ColaboradorBottomNavBar(navController, unreadCount) },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Dinámico con Gradiente
            HeaderSection(colaborador, unreadCount, navController)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp) // Efecto de superposición sobre el header
                    .padding(horizontal = 16.dp)
            ) {
                // 2. Tarjeta de Estado / Skills Dinámica
                colaborador?.let { colab ->
                    DynamicSkillsCard(
                        colaborador = colab,
                        onSeeAllClick = { navController.navigate(Routes.COLABORADOR_SKILLS) }
                    )
                } ?: LoadingCard()

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Accesos Rápidos (Grid)
                Text(
                    "¿Qué quieres hacer hoy?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Vacantes",
                        icon = Icons.Outlined.WorkOutline,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.VACANTES_COLABORADOR) }
                    )
                    QuickActionCard(
                        title = "Solicitudes",
                        icon = Icons.Outlined.Assignment,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SOLICITUDES_COLABORADOR) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Logout estilizado (Ocupa todo el ancho)
                QuickActionCard(
                    title = "Salir",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    color = Color(0xFFEF5350),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLogout
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Últimas Alertas (Dinámico)
                if (alertasUi.isNotEmpty()) {
                    Text(
                        "Novedades para ti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    
                    // Mostramos solo la más reciente
                    val latestAlert = alertasUi.first()
                    NotificationPreviewCard(
                        title = latestAlert.alerta.tipo.replace("_", " "),
                        message = (latestAlert.alerta.detalle as? Map<*, *>)?.get("descripcion")?.toString() ?: "Tienes una nueva notificación",
                        isUnread = !latestAlert.isVisto,
                        onClick = { navController.navigate(Routes.ALERTAS_COLABORADOR) }
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(colaborador: ColaboradorReadDto?, unreadCount: Int, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(TCSBlue, TCSLightBlue)
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar con borde
                Surface(
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.auth_logo_tata),
                        contentDescription = "Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.background(Color.White)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Hola, ${colaborador?.nombres?.split(" ")?.firstOrNull() ?: "Colaborador"}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = colaborador?.rolLaboral ?: "Cargando...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Campanita en el header
            IconButton(
                onClick = { navController.navigate(Routes.ALERTAS_COLABORADOR) },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge { Text(unreadCount.toString()) }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de skills con cálculo de progreso real basado en pesos
 */
@Composable
fun DynamicSkillsCard(colaborador: ColaboradorReadDto, onSeeAllClick: () -> Unit) {
    // 1. Cálculo del progreso dinámico (Solo Skills TÉCNICAS)
    // Filtramos la lista para que solo incluya skills donde tipo == "TECNICO"
    val skills = remember(colaborador.skills) {
        colaborador.skills.filter { 
            it.tipo.equals("TECNICO", ignoreCase = true) 
        }
    }
    
    // Pesos: 1=Básico, 2=Intermedio, 3=Avanzado (Max)
    val maxWeightPerSkill = 3
    
    val growthData = remember(skills) {
        if (skills.isNullOrEmpty()) {
            Pair(0f, 0) // 0% progreso, 0 skills
        } else {
            // Peso Máximo = CantidadDeSkills * 3
            val totalMaxWeight = skills.size * maxWeightPerSkill
            
            // TotalPesoActual
            val currentTotalWeight = skills.sumOf { skill ->
                // Asumimos nivel 1,2,3. Si llega 4 (Experto), lo capamos a 3 para el 100% de esta métrica
                // o usamos el valor real si quieres permitir >100% (super achievement)
                // Aquí lo limitamos a 3 para que sea consistente con "Avanzado = Meta"
                if (skill.nivel > 3) 3 else skill.nivel
            }
            
            // Cálculo Porcentaje
            val percentage = (currentTotalWeight.toFloat() / totalMaxWeight.toFloat()).coerceIn(0f, 1f)
            Pair(percentage, skills.size)
        }
    }

    val (targetProgress, skillsCount) = growthData

    // Animación de la barra
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1500), 
        label = "Progress"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Título y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TCSBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tu Crecimiento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Surface(
                    color = BadgeGreenBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$skillsCount Skills Téc.",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = BadgeGreenText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progreso y Porcentaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nivel Técnico General", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = TextSecondary
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%", 
                    style = MaterialTheme.typography.bodySmall, 
                    fontWeight = FontWeight.Bold, 
                    color = TCSBlue
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TCSBlue,
                trackColor = Color(0xFFE0E0E0),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mensaje motivacional dinámico según el progreso
            val motivationText = when {
                targetProgress == 0f -> "¡Empieza a agregar tus skills técnicas!"
                targetProgress < 0.3f -> "Buen comienzo técnico, sigue aprendiendo."
                targetProgress < 0.7f -> "Vas muy bien, ¡sigue así!"
                targetProgress < 0.9f -> "¡Estás cerca de la excelencia técnica!"
                else -> "¡Increíble! Eres un experto técnico."
            }
            
            Text(
                text = motivationText,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSeeAllClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TCSBlue.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    "Ver Detalle de Skills",
                    color = TCSBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun NotificationPreviewCard(
    title: String,
    message: String,
    isUnread: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) WarningOrange else Color.Gray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.White, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = TCSBlue)
    }
}
