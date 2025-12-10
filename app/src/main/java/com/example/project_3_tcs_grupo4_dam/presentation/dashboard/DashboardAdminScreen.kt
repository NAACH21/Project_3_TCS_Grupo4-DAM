package com.example.project_3_tcs_grupo4_dam.presentation.dashboard

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.BrechaSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.DashboardData
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.SkillDemandadoDto
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar

// Colores del Dashboard
private val PrimaryColor = Color(0xFF1976D2)
private val SecondaryColor = Color(0xFF42A5F5)
private val BackgroundColor = Color(0xFFF5F7FA)
private val SuccessColor = Color(0xFF4CAF50)
private val WarningColor = Color(0xFFFFA726)
private val DangerColor = Color(0xFFEF5350)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    var dashboardState by remember { mutableStateOf<DashboardState>(DashboardState.Loading) }

    LaunchedEffect(Unit) {
        viewModel.cargarMetricas()
    }

    DisposableEffect(viewModel) {
        val observer = androidx.lifecycle.Observer<DashboardState> { newState ->
            dashboardState = newState
        }
        viewModel.state.observeForever(observer)
        onDispose {
            viewModel.state.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Gerencial", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = BackgroundColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = dashboardState) {
                is DashboardState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is DashboardState.Success -> DashboardContent(currentState.data)
                is DashboardState.Error -> ErrorContent(currentState.message) { viewModel.cargarMetricas() }
            }
        }
    }
}

@Composable
fun DashboardContent(data: DashboardData) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. KPIs Principales (Tarjetas Superiores)
        item {
            Text("Resumen General", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCardModern(
                    title = "Vacantes",
                    value = data.metricasVacantes?.totalVacantes?.toString() ?: "0",
                    icon = Icons.Default.BusinessCenter,
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f)
                )
                KpiCardModern(
                    title = "Abiertas",
                    value = data.metricasVacantes?.vacantesAbiertas?.toString() ?: "0",
                    icon = Icons.Default.CheckCircle,
                    color = SuccessColor,
                    modifier = Modifier.weight(1f)
                )
                KpiCardModern(
                    title = "Match %",
                    value = "${(data.metricasMatching?.porcentajeMatchPromedio?.times(100))?.toInt() ?: 0}%",
                    icon = Icons.Default.Group,
                    color = WarningColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. Gráfico de Dona: Vacantes por Urgencia
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vacantes por Urgencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Datos simulados o extraídos del mapa si existe
                    val urgenciaMap = data.metricasVacantes?.vacantesPorUrgencia ?: mapOf("Alta" to 5, "Media" to 3, "Baja" to 2)
                    DonutChart(
                        data = urgenciaMap.mapValues { it.value.toFloat() },
                        colors = listOf(DangerColor, WarningColor, SuccessColor)
                    )
                }
            }
        }

        // 3. Gráfico de Barras: Top Skills
        item {
            Text("Top Skills Demandados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val skills = data.skillsMasDemandados?.take(5) ?: emptyList()
                    if (skills.isNotEmpty()) {
                        BarChart(skills)
                    } else {
                        Text("No hay datos de skills", color = Color.Gray)
                    }
                }
            }
        }

        // 4. Brechas de Habilidades
        item {
            Text("Brechas Críticas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(data.brechasPrioritarias ?: emptyList()) { brecha ->
                    BrechaCardModern(brecha)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// --- COMPONENTES GRÁFICOS ---

@Composable
fun DonutChart(
    data: Map<String, Float>,
    colors: List<Color>
) {
    val total = data.values.sum()
    val proportions = data.values.map { if (total == 0f) 0f else it / total }
    val sweepAngles = proportions.map { it * 360f }

    var animationPlayed by remember { mutableStateOf(false) }
    
    // Leyenda y Gráfico Lado a Lado
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // El Gráfico
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                var startAngle = -90f
                data.keys.forEachIndexed { index, _ ->
                    val sweep = sweepAngles[index]
                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Butt),
                        size = Size(size.width, size.height),
                        topLeft = Offset(0f, 0f)
                    )
                    startAngle += sweep
                }
            }
            // Texto central
            Text(
                text = "${total.toInt()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        // Leyenda
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.keys.forEachIndexed { index, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(colors.getOrElse(index) { Color.Gray }, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label (${data[label]?.toInt()})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    LaunchedEffect(key1 = true) { animationPlayed = true }
}

@Composable
fun BarChart(skills: List<SkillDemandadoDto>) {
    val maxVal = skills.maxOfOrNull { it.cantidadVacantes }?.toFloat() ?: 1f
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        skills.forEachIndexed { index, skill ->
            val animatedProgress by animateFloatAsState(
                targetValue = skill.cantidadVacantes / maxVal,
                animationSpec = tween(durationMillis = 1000, delayMillis = index * 100)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = skill.nombreSkill,
                    modifier = Modifier.width(100.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .weight(1f)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(
                                color = if (index % 2 == 0) PrimaryColor else SecondaryColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Text(
                        text = "${skill.cantidadVacantes}",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (animatedProgress > 0.1f) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

// --- TARJETAS UI ---

@Composable
fun KpiCardModern(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            
            Column {
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BrechaCardModern(brecha: BrechaSkillDto) {
    Card(
        modifier = Modifier.width(180.dp).height(130.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(DangerColor, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Brecha Crítica", color = DangerColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            
            Text(brecha.nombreSkill, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            
            Column {
                Text("Brecha: -${String.format("%.1f", brecha.brechaPromedio)}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text("${brecha.colaboradoresAfectados} afectados", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("No se pudo cargar el dashboard", style = MaterialTheme.typography.titleMedium)
        Text(message, color = Color.Gray, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}
