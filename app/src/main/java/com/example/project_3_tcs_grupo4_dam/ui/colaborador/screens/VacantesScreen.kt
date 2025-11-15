package com.example.project_3_tcs_grupo4_dam.ui.colaborador.screens

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


// ======================================================
// DATA CLASS
// ======================================================
data class VacanteItem(
    val id: Int,
    val titulo: String,
    val area: VacanteArea,
    val nivel: String,
    val modalidad: String,
    val tiempoPublicado: String
)

enum class VacanteArea {
    DATA, TI, NEGOCIOS, OTROS
}


// ======================================================
// MOCK DATA
// ======================================================
val mockVacantes = listOf(
    VacanteItem(
        id = 1,
        titulo = "Analista de Datos",
        area = VacanteArea.DATA,
        nivel = "Intermedio",
        modalidad = "Remoto",
        tiempoPublicado = "Hace 2 días"
    ),
    VacanteItem(
        id = 2,
        titulo = "Desarrollador Backend",
        area = VacanteArea.TI,
        nivel = "Junior",
        modalidad = "Híbrido",
        tiempoPublicado = "Hace 5 días"
    ),
    VacanteItem(
        id = 3,
        titulo = "Gestor de Proyectos",
        area = VacanteArea.NEGOCIOS,
        nivel = "Senior",
        modalidad = "Presencial",
        tiempoPublicado = "Ayer"
    )
)


// ======================================================
// MAIN SCREEN
// ======================================================
@Composable
fun VacantesScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Vacantes Disponibles",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(mockVacantes) { vacante ->
                VacanteCard(vacante)
            }
        }
    }
}


// ======================================================
// CARD COMPONENT
// ======================================================
@Composable
fun VacanteCard(v: VacanteItem) {

    val icon = when (v.area) {
        VacanteArea.DATA -> Icons.Filled.Analytics
        VacanteArea.TI -> Icons.Filled.Computer
        VacanteArea.NEGOCIOS -> Icons.Filled.BusinessCenter
        VacanteArea.OTROS -> Icons.Filled.DataUsage
    }

    val colorArea = when (v.area) {
        VacanteArea.DATA -> Color(0xFF1976D2)
        VacanteArea.TI -> Color(0xFF5E35B1)
        VacanteArea.NEGOCIOS -> Color(0xFF00897B)
        VacanteArea.OTROS -> Color(0xFF455A64)
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

                InfoChip(label = v.modalidad)
                InfoChip(label = "Nivel: ${v.nivel}")
                InfoChip(label = v.tiempoPublicado)
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
// LITTLE CHIP COMPONENT
// ======================================================
@Composable
fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE8EAF6))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 13.sp)
    }
}
