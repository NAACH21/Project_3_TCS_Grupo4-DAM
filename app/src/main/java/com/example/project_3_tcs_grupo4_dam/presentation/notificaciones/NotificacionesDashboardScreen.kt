package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.model.ColorPrioridad
import com.example.project_3_tcs_grupo4_dam.data.model.TipoOrigenAlerta
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// Colores personalizados
private val BackgroundColor = Color(0xFFF7F4F2)
private val TCSBlue = Color(0xFF00549F)
private val TextGray = Color(0xFF6D6D6D)
private val ReadGreen = Color(0xFF4CAF50) // Color verde para leídos

/**
 * Screen principal del Dashboard de Notificaciones Unificado
 * Esta es la nueva implementación que consume AlertaDashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Obtener datos de sesión
    val rolUsuario = sessionManager.getRol() ?: "COLABORADOR"
    
    // FIX: Considerar tanto ADMIN como MANAGER como roles administrativos
    val esAdmin = rolUsuario.equals("ADMIN", ignoreCase = true) || rolUsuario.equals("MANAGER", ignoreCase = true)
    
    // FIX: Determinar ruta Home correcta según rol
    val homeRoute = if (rolUsuario.equals("MANAGER", ignoreCase = true)) Routes.MANAGER_HOME else Routes.ADMIN_HOME
    
    val userId = sessionManager.getColaboradorId()

    // Instanciar ViewModel
    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )

    // Observar estados del Dashboard
    val alertasDashboard by viewModel.alertasDashboard.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Cargar notificaciones al iniciar
    LaunchedEffect(Unit) {
        viewModel.cargarNotificaciones(esAdmin, userId)
    }

    // Estado para diálogo de detalle
    var selectedAlerta by remember { mutableStateOf<AlertaDashboard?>(null) }

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            if (esAdmin) {
                // Mostrar BottomBar de Admin con la ruta Home correcta
                BottomNavBar(navController = navController, homeRoute = homeRoute)
            } else {
                // Mostrar BottomBar de Colaborador con Badge
                ColaboradorBottomNavBar(navController, unreadCount)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Dashboard de Notificaciones",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFFEF6C00)
                            ) {
                                Text(unreadCount.toString())
                            }
                        }
                    }
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
            when {
                isLoading && alertasDashboard.isEmpty() -> { // Mostrar loader solo si no hay datos previos
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TCSBlue
                    )
                }
                errorMessage != null && alertasDashboard.isEmpty() -> { // Mostrar error solo si no hay datos
                    ErrorView(
                        message = errorMessage!!,
                        onRetry = {
                            viewModel.cargarNotificaciones(esAdmin, userId)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                alertasDashboard.isEmpty() && !isLoading -> {
                    EmptyStateView(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(alertasDashboard) { alerta ->
                            AlertaDashboardCard(
                                alerta = alerta,
                                onClick = {
                                    selectedAlerta = alerta
                                    // Marcar como leída PERSISTENTEMENTE cuando se abre el detalle
                                    if (alerta.activa) {
                                        viewModel.marcarDashboardComoLeida(alerta.idReferencia)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo de detalle
        selectedAlerta?.let { alerta ->
            AlertaDetalleDialog(
                alerta = alerta,
                onDismiss = { selectedAlerta = null }
            )
        }
    }
}

/**
 * Card individual para AlertaDashboard
 */
@Composable
fun AlertaDashboardCard(
    alerta: AlertaDashboard,
    onClick: () -> Unit
) {
    // Obtener color de fondo según prioridad (Solo para activas/no leídas)
    // Si está leída, usamos un fondo blanco o gris claro para diferenciar
    val backgroundColor = if (alerta.activa) {
        when (alerta.colorPrioridad) {
            ColorPrioridad.ROJO -> Color(0xFFFFEBEE)
            ColorPrioridad.AMARILLO -> Color(0xFFFFFDE7)
            ColorPrioridad.VERDE -> Color(0xFFE8F5E9)
        }
    } else {
        Color.White // Fondo neutro para leídos
    }

    // Obtener icono según tipo
    val (icono, iconColor) = when (alerta.tipoOrigen) {
        TipoOrigenAlerta.SKILL_GAP -> Icons.AutoMirrored.Rounded.TrendingUp to Color(0xFFEF6C00)
        TipoOrigenAlerta.CERTIFICACION -> Icons.Rounded.School to Color(0xFF1976D2)
        TipoOrigenAlerta.VACANTE_DISPONIBLE -> Icons.Rounded.Work to Color(0xFF388E3C)
        TipoOrigenAlerta.GENERICA -> Icons.Rounded.Notifications to Color(0xFF757575)
    }

    // Color del icono: si está leído, lo atenuamos un poco
    val finalIconColor = if (alerta.activa) iconColor else iconColor.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alerta.activa) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(finalIconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = finalIconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Contenido
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título con indicador de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alerta.titulo,
                        fontSize = 16.sp,
                        fontWeight = if (alerta.activa) FontWeight.Bold else FontWeight.Normal, // Negrita solo si no leído
                        color = if (alerta.activa) Color.Black else TextGray,
                        modifier = Modifier.weight(1f)
                    )

                    // Indicador de estado (Punto rojo vs Verde)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (alerta.activa) Color(0xFFFF5252) // Rojo (No leído)
                                else ReadGreen // Verde (Leído) - SOLICITUD DEL USUARIO
                            )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mensaje
                Text(
                    text = alerta.mensaje,
                    fontSize = 14.sp,
                    color = TextGray,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha
                Text(
                    text = alerta.fecha,
                    fontSize = 12.sp,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Vista de estado vacío
 */
@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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
            text = "No tienes notificaciones",
            color = TextGray,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Vista de error con botón de reintento
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Error,
            contentDescription = null,
            tint = Color(0xFFEF5350),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = TextGray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = TCSBlue
            )
        ) {
            Text("Reintentar")
        }
    }
}

/**
 * Diálogo con detalle de la notificación
 */
@Composable
fun AlertaDetalleDialog(
    alerta: AlertaDashboard,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = alerta.titulo,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(text = alerta.mensaje)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fecha: ${alerta.fecha}",
                    fontSize = 12.sp,
                    color = TextGray
                )
                Text(
                    text = "Tipo: ${alerta.tipoOrigen.name.replace("_", " ")}",
                    fontSize = 12.sp,
                    color = TextGray
                )
                // Indicador explícito de estado en el detalle
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (alerta.activa) Color(0xFFFF5252) else ReadGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (alerta.activa) "No leído" else "Leído",
                        fontSize = 12.sp,
                        color = if (alerta.activa) Color(0xFFFF5252) else ReadGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TCSBlue)
            }
        }
    )
}
