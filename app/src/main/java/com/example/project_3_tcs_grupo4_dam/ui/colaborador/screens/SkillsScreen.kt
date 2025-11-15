package com.example.project_3_tcs_grupo4_dam.ui.colaborador.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
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
data class SkillItem(
    val name: String,
    val level: Int,       // 1–4
    val status: SkillStatus
)

enum class SkillStatus {
    APROBADO, PENDIENTE, EN_PROCESO
}


// ======================================================
// MOCK DATA
// ======================================================
val mockSkills = listOf(
    SkillItem("Excel Avanzado", 4, SkillStatus.APROBADO),
    SkillItem("SQL Intermedio", 3, SkillStatus.EN_PROCESO),
    SkillItem("Python Básico", 2, SkillStatus.PENDIENTE),
    SkillItem("Power BI", 3, SkillStatus.APROBADO)
)


// ======================================================
// MAIN SCREEN
// ======================================================
@Composable
fun SkillsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis Skills",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockSkills) { skill ->
                SkillCard(skill)
            }
        }
    }
}


// ======================================================
// CARD COMPONENT
// ======================================================
@Composable
fun SkillCard(skill: SkillItem) {

    val bgColor = when (skill.status) {
        SkillStatus.APROBADO -> Color(0xFFE7F8EA)
        SkillStatus.EN_PROCESO -> Color(0xFFFFF4E5)
        SkillStatus.PENDIENTE -> Color(0xFFFFE5E5)
    }

    val icon = when (skill.status) {
        SkillStatus.APROBADO -> Icons.Filled.CheckCircle
        SkillStatus.EN_PROCESO -> Icons.Filled.Timelapse
        SkillStatus.PENDIENTE -> Icons.Filled.Pending
    }

    val iconColor = when (skill.status) {
        SkillStatus.APROBADO -> Color(0xFF2E7D32)
        SkillStatus.EN_PROCESO -> Color(0xFFFF9800)
        SkillStatus.PENDIENTE -> Color(0xFFD32F2F)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // ICONO
                Icon(
                    icon,
                    contentDescription = skill.name,
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // TITULO
                Column {
                    Text(
                        text = skill.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nivel ${skill.level}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BARRA DE NIVEL
            LinearProgressIndicator(
                progress = skill.level / 4f,
                color = iconColor,
                trackColor = Color.LightGray.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}
