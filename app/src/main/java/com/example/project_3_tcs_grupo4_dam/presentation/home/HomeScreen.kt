package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val primaryBlue = Color(0xFF0A63C2)
    val backgroundBlue = Color(0xFF0E4F9C)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = Routes.HOME
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F7FB))
        ) {

            // 🔵 HEADER
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

                // Avatar
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

            Spacer(modifier = Modifier.height(14.dp))

            // 🔹 TARJETAS COMPACTAS
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardSmall(
                        title = "Colaboradores",
                        value = "142",
                        iconColor = Color(0xFF245DFF)
                    )
                    MetricCardSmall(
                        title = "Evaluaciones",
                        value = "8",
                        iconColor = Color(0xFFFF9800)
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardSmall(
                        title = "Vacantes",
                        value = "5",
                        iconColor = Color(0xFF4CAF50)
                    )
                    MetricCardSmall(
                        title = "Cobertura",
                        value = "78%",
                        iconColor = Color(0xFF9C27B0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Acciones rápidas",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔸 ACCIONES RÁPIDAS EN 2 COLUMNAS
            QuickActionsGrid(navController)
        }
    }
}

@Composable
fun MetricCardSmall(title: String, value: String, iconColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(150.dp)
            .height(95.dp)
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
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavController) {
    val buttons = listOf(
        "Matching Inteligente" to Color(0xFF0A63C2),
        "Dashboard General" to Color(0xFF0A63C2),
        "Brechas de Skills" to Color(0xFFFF5722),
        "Alertas Automáticas" to Color(0xFFD32F2F)
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        for (i in buttons.indices step 2) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val (text1, color1) = buttons[i]
                QuickButton(
                    text = text1,
                    color = color1,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (text1 == "Matching Inteligente") {
                            navController.navigate(Routes.MATCHING)
                        }
                    }
                )

                if (i + 1 < buttons.size) {
                    val (text2, color2) = buttons[i + 1]
                    QuickButton(
                        text = text2,
                        color = color2,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (text2 == "Matching Inteligente") {
                                navController.navigate(Routes.MATCHING)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
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
            fontWeight = FontWeight.SemiBold
        )
    }
}