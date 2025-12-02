package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

// Colores personalizados
private val BackgroundColor = Color(0xFFF7F4F2)
private val TCSBlue = Color(0xFF00549F)
private val TextGray = Color(0xFF6D6D6D)

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
    val esAdmin = rolUsuario.equals("ADMIN", ignoreCase = true)
    val userId = sessionManager.getColaboradorId()

    // Instanciar ViewModel
    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
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
            ColaboradorBottomNavBar(navController, unreadCount)
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
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TCSBlue
                    )
                }
                errorMessage != null -> {
                    ErrorView(
                        message = errorMessage!!,
                        onRetry = {
                            viewModel.cargarNotificaciones(esAdmin, userId)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                alertasDashboard.isEmpty() -> {
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
                                onClick = { selectedAlerta = alerta }
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
    // Obtener color de fondo según prioridad
    val backgroundColor = when (alerta.colorPrioridad) {
        ColorPrioridad.ROJO -> Color(0xFFFFEBEE)
        ColorPrioridad.AMARILLO -> Color(0xFFFFFDE7)
        ColorPrioridad.VERDE -> Color(0xFFE8F5E9)
    }

    // Obtener icono según tipo
    val (icono, iconColor) = when (alerta.tipoOrigen) {
        TipoOrigenAlerta.SKILL_GAP -> Icons.Rounded.TrendingUp to Color(0xFFEF6C00)
        TipoOrigenAlerta.CERTIFICACION -> Icons.Rounded.School to Color(0xFF1976D2)
        TipoOrigenAlerta.VACANTE_DISPONIBLE -> Icons.Rounded.Work to Color(0xFF388E3C)
        TipoOrigenAlerta.GENERICA -> Icons.Rounded.Notifications to Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Contenido
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título con indicador de no leída
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alerta.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    if (alerta.activa) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252))
                        )
                    }
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
                Divider()
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
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TCSBlue)
            }
        }
    )
}

