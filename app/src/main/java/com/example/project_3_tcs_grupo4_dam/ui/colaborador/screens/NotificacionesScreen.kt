package com.example.project_3_tcs_grupo4_dam.ui.colaborador.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ErrorOutline

// ======================================
// DATA CLASS
// ======================================
data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val type: NotificationType = NotificationType.INFO,
    val unread: Boolean = true
)

enum class NotificationType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}


// ======================================
// MOCK LIST
// ======================================
val mockNotifications = listOf(
    NotificationItem(
        id = 1,
        title = "Skill Aprobado",
        description = "Tu skill Excel Avanzado ha sido validado por RRHH.",
        type = NotificationType.SUCCESS
    ),
    NotificationItem(
        id = 2,
        title = "Falta evidencia",
        description = "Debes enviar una evidencia válida para SQL Intermedio.",
        type = NotificationType.WARNING
    ),
    NotificationItem(
        id = 3,
        title = "Vacante Recomendada",
        description = "Nueva vacante Analista de Datos recomendada según tus skills.",
        type = NotificationType.INFO
    ),
    NotificationItem(
        id = 4,
        title = "Evidencia Rechazada",
        description = "Tu evidencia para Power BI fue rechazada.",
        type = NotificationType.ERROR
    )
)


// ======================================
// MAIN SCREEN
// ======================================
@Composable
fun NotificacionesScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Notificaciones",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockNotifications) { noti ->
                NotificationCard(noti)
            }
        }
    }
}


// ======================================
// CARD COMPONENT
// ======================================
@Composable
fun NotificationCard(noti: NotificationItem) {

    val bgColor = when (noti.type) {
        NotificationType.SUCCESS -> Color(0xFFE7F8EA)
        NotificationType.WARNING -> Color(0xFFFFF1D6)
        NotificationType.ERROR   -> Color(0xFFFFE3E3)
        NotificationType.INFO    -> Color(0xFFDDEBFF)
    }

    val icon = when (noti.type) {
        NotificationType.SUCCESS -> Icons.Filled.CheckCircle
        NotificationType.WARNING -> Icons.Filled.Warning
        NotificationType.ERROR -> Icons.Filled.ErrorOutline
        NotificationType.INFO    -> Icons.Filled.Info
    }

    val iconColor = when (noti.type) {
        NotificationType.SUCCESS -> Color(0xFF2E7D32)
        NotificationType.WARNING -> Color(0xFFED6C02)
        NotificationType.ERROR   -> Color(0xFFD32F2F)
        NotificationType.INFO    -> Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { /* TODO abrir detalle si quieres */ },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            // ICONO
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = noti.title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // TEXTO
            Column {
                Text(
                    text = noti.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = noti.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
