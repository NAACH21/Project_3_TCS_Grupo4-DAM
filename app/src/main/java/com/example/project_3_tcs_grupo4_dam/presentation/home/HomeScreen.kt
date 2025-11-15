package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@Composable
fun HomeScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }

    val primaryBlue = Color(0xFF0A63C2)
    val backgroundBlue = Color(0xFF0E4F9C)
    val bgLight = Color(0xFFF6F7FB)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController)
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
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Hola, Leslie 👋",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bienvenida al sistema de gestión de talento",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Avatar circular en la esquina superior derecha
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "L",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                        text = "Brechas de Skills",
                        color = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.SKILLS) }
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
    title: String,
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