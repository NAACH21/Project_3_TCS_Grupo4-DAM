package com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudBottomSheet(
    onDismiss: () -> Unit,
    onSeleccionarNuevaSkill: () -> Unit,
    onSeleccionarActualizarSkill: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Nueva Solicitud",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecciona el tipo de solicitud:",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TipoSolicitudCard(
                titulo = "Nueva Skill",
                descripcion = "Agregar una nueva skill con certificado de respaldo",
                icon = Icons.Default.AddCircle,
                onClick = {
                    onSeleccionarNuevaSkill()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TipoSolicitudCard(
                titulo = "Actualizar Skill",
                descripcion = "Actualizar nivel de skill existente con nuevo certificado",
                icon = Icons.Default.TrendingUp,
                onClick = {
                    onSeleccionarActualizarSkill()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TipoSolicitudCard(
    titulo: String,
    descripcion: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TCSBlue,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}
