package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Colores personalizados para estados
private val BackgroundColor = Color(0xFFF7F4F2)

// Colores para NO VISTO (Naranja)
private val UnreadCardColor = Color(0xFFFFF3E0)
private val UnreadIconColor = Color(0xFFEF6C00)
private val UnreadBorderColor = Color(0xFFFFB74D)

// Colores para VISTO (Verde)
private val ReadCardColor = Color(0xFFE8F5E9)
private val ReadIconColor = Color(0xFF2E7D32)
private val ReadBorderColor = Color(0xFFA5D6A7)

private val TextGray = Color(0xFF6D6D6D)
private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Instanciamos el ViewModel
    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Pasamos el contexto para el LocalAlertManager
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )

    // Observamos la lista UI enriquecida y el contador calculado
    val alertasUi by viewModel.alertasUi.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estado para manejar el diálogo de detalle
    var selectedAlertaUi by remember { mutableStateOf<AlertaUiState?>(null) }

    Scaffold(
        containerColor = BackgroundColor, 
        // Pasamos el contador de no leídos al Badge
        bottomBar = { 
            ColaboradorBottomNavBar(navController, unreadCount) 
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Notificaciones", 
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = UnreadIconColor
                )
            } else if (alertasUi.isEmpty()) {
                // Estado vacío estilizado
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones nuevas",
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Lista de notificaciones
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 100.dp 
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alertasUi) { alertaUi ->
                        // Extraemos ID para la lógica de click
                        val idStr = extraerIdSimple(alertaUi.alerta.id)

                        AlertaCard(
                            alertaUi = alertaUi,
                            onClick = { 
                                // 1. Marcar como visto si no lo está
                                if (!alertaUi.isVisto && idStr != null) {
                                    viewModel.marcarComoVisto(idStr)
                                }
                                // 2. Abrir diálogo con detalle
                                selectedAlertaUi = alertaUi
                            }
                        )
                    }
                }
            }
        }
    }

    // Mostrar el diálogo si hay una alerta seleccionada
    if (selectedAlertaUi != null) {
        DetalleNotificacionDialog(
            alerta = selectedAlertaUi!!.alerta,
            onDismiss = { selectedAlertaUi = null }
        )
    }
}

@Composable
fun DetalleNotificacionDialog(
    alerta: AlertaDto,
    onDismiss: () -> Unit
) {
    val detalleMap = alerta.detalle as? Map<*, *>
    val descripcion = detalleMap?.get("descripcion")?.toString() ?: "Sin descripción"
    val skillsFaltantes = detalleMap?.get("skillsFaltantes") as? List<*>
    val fechaProxima = detalleMap?.get("fechaProximaEvaluacion")?.toString()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TCSBlue)
        },
        title = {
            Text(
                text = alerta.tipo.replace("_", " "),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Descripción completa
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Divider(color = Color.LightGray)

                // Skills Faltantes (si aplica)
                if (!skillsFaltantes.isNullOrEmpty()) {
                    Text(
                        text = "Skills Requeridos:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TCSBlue
                    )
                    skillsFaltantes.forEach { skillObj ->
                        val skillMap = skillObj as? Map<*, *>
                        val nombre = skillMap?.get("nombre")?.toString() ?: "Skill"
                        val nivel = skillMap?.get("nivelRequerido")?.toString() ?: ""
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Warning, null, tint = UnreadIconColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$nombre (Nivel $nivel)", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Divider(color = Color.LightGray)
                }

                // Fechas
                if (fechaProxima != null) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Próxima Evaluación:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(formatearFechaCorta(fechaProxima), style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                alerta.fechaCreacion?.let {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Recibido:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(formatearFechaCorta(it), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TCSBlue)
            ) {
                Text("Entendido")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// Helper simple para la vista
private fun extraerIdSimple(valor: Any?): String? {
    if (valor == null) return null
    return try {
        when (valor) {
            is String -> valor
            is Map<*, *> -> valor["\$oid"]?.toString() ?: valor["oid"]?.toString()
            else -> valor.toString()
        }
    } catch (e: Exception) { null }
}

private fun formatearFechaCorta(fechaIso: String): String {
    return try {
        // Intentamos parsear ISO simple
        fechaIso.take(10) // "YYYY-MM-DD"
    } catch (e: Exception) {
        fechaIso
    }
}

@Composable
fun AlertaCard(
    alertaUi: AlertaUiState, 
    onClick: () -> Unit
) {
    val alerta = alertaUi.alerta
    val isVisto = alertaUi.isVisto

    // Configuración visual dinámica
    val containerColor = if (isVisto) ReadCardColor else UnreadCardColor
    val iconColor = if (isVisto) ReadIconColor else UnreadIconColor
    val borderColor = if (isVisto) ReadBorderColor else UnreadBorderColor
    val iconVector = if (isVisto) Icons.Rounded.CheckCircle else Icons.Rounded.Warning
    
    // Parseo robusto del detalle
    val detalleMap = alerta.detalle as? Map<*, *>
    val descripcion = detalleMap?.get("descripcion")?.toString() ?: ""
    val skillsFaltantes = detalleMap?.get("skillsFaltantes") as? List<*>

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onClick() }, // Click para marcar como visto y abrir detalle
        shape = RoundedCornerShape(18.dp), 
        colors = CardDefaults.cardColors(
            containerColor = containerColor 
        ),
        // Borde para distinguir mejor
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isVisto) 0.dp else 4.dp 
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = if (isVisto) "Leído" else "No leído",
                        tint = iconColor, 
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tipo de alerta
                    Text(
                        text = alerta.tipo.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Badge "NUEVO" si no ha sido visto
                    if (!isVisto) {
                        Surface(
                            color = UnreadIconColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NUEVO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Estado Backend + Estado Local
                Text(
                    text = if (isVisto) "Leído - Toca para ver detalle" else "Toca para ver detalle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )

                // Descripción principal (Resumen)
                if (descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray.copy(alpha = 0.9f),
                        maxLines = 2, // Limitamos líneas en la card
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
