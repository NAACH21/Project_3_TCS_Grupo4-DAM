package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar


// ======================================================
// DATA CLASS PARA USUARIO COLABORADOR
// ======================================================
data class VacanteColaboradorItem(
    val id: Int,
    val titulo: String,
    val area: VacanteColaboradorArea,
    val nivel: String,
    val modalidad: String,
    val tiempoPublicado: String
)

enum class VacanteColaboradorArea {
    DATA, TI, NEGOCIOS, OTROS
}


// ======================================================
// MOCK DATA PARA COLABORADORES
// ======================================================
val mockVacantesColaborador = listOf(
    VacanteColaboradorItem(
        id = 1,
        titulo = "Analista de Datos",
        area = VacanteColaboradorArea.DATA,
        nivel = "Intermedio",
        modalidad = "Remoto",
        tiempoPublicado = "Hace 2 días"
    ),
    VacanteColaboradorItem(
        id = 2,
        titulo = "Desarrollador Backend",
        area = VacanteColaboradorArea.TI,
        nivel = "Junior",
        modalidad = "Híbrido",
        tiempoPublicado = "Hace 5 días"
    ),
    VacanteColaboradorItem(
        id = 3,
        titulo = "Gestor de Proyectos",
        area = VacanteColaboradorArea.NEGOCIOS,
        nivel = "Senior",
        modalidad = "Presencial",
        tiempoPublicado = "Ayer"
    )
)


// ======================================================
// PANTALLA PARA COLABORADOR (Usuario Normal)
// ======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantesScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacantes Disponibles") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(mockVacantesColaborador) { vacante ->
                    VacanteColaboradorCard(vacante)
                }
            }
        }
    }
}


// ======================================================
// CARD COMPONENT PARA COLABORADOR
// ======================================================
@Composable
fun VacanteColaboradorCard(v: VacanteColaboradorItem) {

    val icon = when (v.area) {
        VacanteColaboradorArea.DATA -> Icons.Filled.Analytics
        VacanteColaboradorArea.TI -> Icons.Filled.Computer
        VacanteColaboradorArea.NEGOCIOS -> Icons.Filled.BusinessCenter
        VacanteColaboradorArea.OTROS -> Icons.Filled.DataUsage
    }

    val colorArea = when (v.area) {
        VacanteColaboradorArea.DATA -> Color(0xFF1976D2)
        VacanteColaboradorArea.TI -> Color(0xFF5E35B1)
        VacanteColaboradorArea.NEGOCIOS -> Color(0xFF00897B)
        VacanteColaboradorArea.OTROS -> Color(0xFF455A64)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { /* TODO: abrir detalle */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // TÍTULO E ÍCONO
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorArea.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = v.titulo,
                        tint = colorArea,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = v.titulo,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = v.area.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DETALLES
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                InfoChipColaborador(label = v.modalidad)
                InfoChipColaborador(label = "Nivel: ${v.nivel}")
                InfoChipColaborador(label = v.tiempoPublicado)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN POSTULAR
            Button(
                onClick = { /* TODO: postular */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorArea)
            ) {
                Text("Postular", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ======================================================
// CHIP COMPONENT PARA COLABORADOR
// ======================================================
@Composable
fun InfoChipColaborador(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE8EAF6))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 13.sp)
    }
}

