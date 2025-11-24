package com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    onAnularClick: ((String) -> Unit)? = null, // ⭐ Callback para anular
    modifier: Modifier = Modifier
) {
    // DEBUG: Log cuando se renderiza cada item
    android.util.Log.d("SolicitudListItem", "Renderizando card: ${solicitud.id} - ${solicitud.tipoSolicitudGeneral}")

    var showMenu by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Verificar si la solicitud puede ser anulada
    val puedeAnular = onAnularClick != null &&
                      (solicitud.estadoSolicitud == "PENDIENTE" || solicitud.estadoSolicitud == "EN_REVISION")

    // Determinar si la card debe verse como anulada
    val estaAnulada = solicitud.estadoSolicitud == "ANULADA"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaAnulada) Color.White.copy(alpha = 0.7f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila superior: Tipo + Estado + Menú
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = getTipoIcon(solicitud.tipoSolicitudGeneral),
                        contentDescription = null,
                        tint = if (estaAnulada) TCSBlue.copy(alpha = 0.5f) else TCSBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Surface(
                        color = getTipoColor(solicitud.tipoSolicitudGeneral).copy(alpha = if (estaAnulada) 0.08f else 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = getTipoLabel(solicitud.tipoSolicitudGeneral),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = getTipoColor(solicitud.tipoSolicitudGeneral).copy(alpha = if (estaAnulada) 0.5f else 1f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    EstadoChip(estado = solicitud.estadoSolicitud)

                    // ⭐ Menú de tres puntos (solo si puede anular)
                    if (puedeAnular) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Más opciones",
                                    tint = TextSecondary
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Anular solicitud",
                                            color = Color(0xFFC62828) // Rojo
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        showConfirmDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo específico de solicitud
            Text(
                text = getTipoSolicitudLabel(solicitud.tipoSolicitud),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (estaAnulada) TextPrimary.copy(alpha = 0.5f) else TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional según tipo
            when (solicitud.tipoSolicitudGeneral) {
                "CERTIFICACION" -> {
                    solicitud.certificacionPropuesta?.let { cert ->
                        Text(
                            text = cert.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (estaAnulada) TextSecondary.copy(alpha = 0.5f) else TextSecondary
                        )
                        Text(
                            text = cert.institucion,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (estaAnulada) TextSecondary.copy(alpha = 0.35f) else TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
                "ACTUALIZACION_SKILLS" -> {
                    solicitud.cambiosSkillsPropuestos?.let { cambios ->
                        if (cambios.isNotEmpty()) {
                            Text(
                                text = "${cambios.size} cambio${if (cambios.size > 1) "s" else ""} de skill propuesto${if (cambios.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (estaAnulada) TextSecondary.copy(alpha = 0.5f) else TextSecondary
                            )
                        }
                    }
                }
                "ENTREVISTA_DESEMPENO" -> {
                    solicitud.datosEntrevistaPropuesta?.let { entrevista ->
                        Text(
                            text = "Periodo: ${entrevista.periodo}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (estaAnulada) TextSecondary.copy(alpha = 0.5f) else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha de creación
            Text(
                text = "Creada: ${formatFecha(solicitud.fechaCreacion)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (estaAnulada) TextSecondary.copy(alpha = 0.35f) else TextSecondary.copy(alpha = 0.7f)
            )
        }
    }

    // ⭐ Diálogo de confirmación para anular
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Anular solicitud",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Seguro que deseas anular esta solicitud? Esta acción no se puede revertir.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onAnularClick?.invoke(solicitud.id)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Text("Anular")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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
        "ANULADA" -> Color(0xFFEEEEEE) to Color(0xFF757575) // ⭐ Gris para anulada
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
        "ANULADA" -> "Anulada" // ⭐ Label para anulada
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
