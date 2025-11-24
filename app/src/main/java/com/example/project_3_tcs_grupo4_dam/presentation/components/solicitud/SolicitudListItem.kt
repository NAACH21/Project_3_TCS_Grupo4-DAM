package com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val TCSBlue = Color(0xFF00549F)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)

@Composable
fun SolicitudListItem(
    solicitud: SolicitudReadDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // DEBUG: Log cuando se renderiza cada item
    android.util.Log.d("SolicitudListItem", "Renderizando card: ${solicitud.id} - ${solicitud.tipoSolicitudGeneral}")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila superior: Tipo + Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = getTipoIcon(solicitud.tipoSolicitudGeneral),
                        contentDescription = null,
                        tint = TCSBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Surface(
                        color = getTipoColor(solicitud.tipoSolicitudGeneral).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = getTipoLabel(solicitud.tipoSolicitudGeneral),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = getTipoColor(solicitud.tipoSolicitudGeneral)
                        )
                    }
                }

                EstadoChip(estado = solicitud.estadoSolicitud)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo específico de solicitud
            Text(
                text = getTipoSolicitudLabel(solicitud.tipoSolicitud),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional según tipo
            when (solicitud.tipoSolicitudGeneral) {
                "CERTIFICACION" -> {
                    solicitud.certificacionPropuesta?.let { cert ->
                        Text(
                            text = cert.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = cert.institucion,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
                "ACTUALIZACION_SKILLS" -> {
                    solicitud.cambiosSkillsPropuestos?.let { cambios ->
                        if (cambios.isNotEmpty()) {
                            Text(
                                text = "${cambios.size} cambio${if (cambios.size > 1) "s" else ""} de skill propuesto${if (cambios.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                "ENTREVISTA_DESEMPENO" -> {
                    solicitud.datosEntrevistaPropuesta?.let { entrevista ->
                        Text(
                            text = "Periodo: ${entrevista.periodo}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha de creación
            Text(
                text = "Creada: ${formatFecha(solicitud.fechaCreacion)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val (backgroundColor, textColor) = getEstadoColors(estado)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = getEstadoLabel(estado),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private fun getTipoIcon(tipo: String): ImageVector {
    return when (tipo) {
        "CERTIFICACION" -> Icons.Default.School
        "ACTUALIZACION_SKILLS" -> Icons.Default.TrendingUp
        "ENTREVISTA_DESEMPENO" -> Icons.Default.Article
        else -> Icons.Default.Article
    }
}

private fun getTipoColor(tipo: String): Color {
    return when (tipo) {
        "CERTIFICACION" -> Color(0xFF2E7D32) // Verde
        "ACTUALIZACION_SKILLS" -> Color(0xFF1976D2) // Azul
        "ENTREVISTA_DESEMPENO" -> Color(0xFF7B1FA2) // Púrpura
        else -> Color(0xFF616161) // Gris
    }
}

private fun getTipoLabel(tipo: String): String {
    return when (tipo) {
        "CERTIFICACION" -> "Certificación"
        "ACTUALIZACION_SKILLS" -> "Skills"
        "ENTREVISTA_DESEMPENO" -> "Entrevista"
        else -> tipo
    }
}

private fun getTipoSolicitudLabel(tipoSolicitud: String): String {
    return when (tipoSolicitud) {
        "NUEVA" -> "Nueva Certificación"
        "RENOVACION" -> "Renovación de Certificación"
        "PERIODICA" -> "Entrevista Periódica"
        "AJUSTE_NIVEL" -> "Ajuste de Nivel de Skills"
        else -> tipoSolicitud.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
    }
}

private fun getEstadoColors(estado: String): Pair<Color, Color> {
    return when (estado) {
        "PENDIENTE" -> Color(0xFFFFF3E0) to Color(0xFFF57F17)
        "EN_REVISION" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "APROBADA" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "RECHAZADA" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "PROGRAMADA" -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
}

private fun getEstadoLabel(estado: String): String {
    return when (estado) {
        "PENDIENTE" -> "Pendiente"
        "EN_REVISION" -> "En Revisión"
        "APROBADA" -> "Aprobada"
        "RECHAZADA" -> "Rechazada"
        "PROGRAMADA" -> "Programada"
        else -> estado
    }
}

private fun formatFecha(fechaIso: String): String {
    return try {
        val fecha = LocalDateTime.parse(fechaIso, DateTimeFormatter.ISO_DATE_TIME)
        fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: Exception) {
        fechaIso.take(10) // Fallback: mostrar solo la fecha
    }
}
