package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud.*
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

private val TCSBlue = Color(0xFF00549F)
private val LightGrayBg = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudAdminScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // ViewModel exclusivo para admin
    val viewModel: SolicitudAdminViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SolicitudAdminViewModel(
                    solicitudesRepository = SolicitudesRepositoryImpl(RetrofitClient.solicitudesApi),
                    sessionManager = sessionManager
                ) as T
            }
        }
    )

    // Estados del ViewModel
    val solicitudes by viewModel.solicitudesFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val isDialogOpen by viewModel.isDialogNuevaEntrevistaOpen.collectAsState()
    val showDetalleDialog by viewModel.showDetalleDialog.collectAsState()
    val showCambioEstadoDialog by viewModel.showCambioEstadoDialog.collectAsState()
    val solicitudSeleccionada by viewModel.solicitudSeleccionada.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Recargar solicitudes cada vez que se vuelve a la pantalla
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry) {
        android.util.Log.d("SolicitudAdminScreen", "LaunchedEffect - Recargando solicitudes")
        viewModel.cargarSolicitudes()
    }

    // Mostrar error si existe
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            viewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solicitudes de Entrevista",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si no hay error de rol
            if (errorMessage != "Esta pantalla es solo para administradores") {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.NUEVA_ENTREVISTA_ADMIN) },
                    containerColor = TCSBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Entrevista")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Verificar si hay error de rol
            if (errorMessage == "Esta pantalla es solo para administradores") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Acceso Restringido",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Esta pantalla es solo para administradores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Filtro de estados
                SolicitudAdminFilterBar(
                    selectedEstado = filtroEstado,
                    onEstadoChange = { viewModel.aplicarFiltroEstado(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido principal
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TCSBlue)
                        }
                    }

                    solicitudes.isEmpty() -> {
                        SolicitudEmptyState(
                            modifier = Modifier.fillMaxSize(),
                            mensaje = "No hay solicitudes de entrevista",
                            submensaje = "Crea la primera solicitud de entrevista de desempeño"
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(solicitudes) { solicitud ->
                                SolicitudListItem(
                                    solicitud = solicitud,
                                    onClick = { viewModel.mostrarDetalleSolicitud(solicitud) },
                                    onCambiarEstadoClick = { solicitudId ->
                                        // Buscar la solicitud completa y abrir diálogo
                                        val sol = solicitudes.find { it.id == solicitudId }
                                        sol?.let { viewModel.abrirCambioEstadoDialog(it) }
                                    }
                                )
                            }

                            // Espacio al final para el FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de detalle de solicitud
    if (showDetalleDialog && solicitudSeleccionada != null) {
        DetalleSolicitudEntrevistaDialog(
            solicitud = solicitudSeleccionada!!,
            onDismiss = { viewModel.cerrarDetalleSolicitud() }
        )
    }

    // Diálogo de cambio de estado
    if (showCambioEstadoDialog && solicitudSeleccionada != null) {
        CambioEstadoDialog(
            solicitud = solicitudSeleccionada!!,
            onDismiss = { viewModel.cerrarCambioEstadoDialog() },
            onConfirmar = { nuevoEstado, observacion ->
                viewModel.actualizarEstadoSolicitud(
                    solicitudSeleccionada!!.id,
                    nuevoEstado,
                    observacion
                )
            }
        )
    }
}

/**
 * Barra de filtros simplificada para admin (solo estados)
 */
@Composable
private fun SolicitudAdminFilterBar(
    selectedEstado: String,
    onEstadoChange: (String) -> Unit
) {
    val estados = listOf(
        "TODOS",
        "PENDIENTE",
        "EN_REVISION",
        "PROGRAMADA",
        "APROBADA",
        "RECHAZADA",
        "ANULADA"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Filtrar por estado",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ScrollableTabRow(
            selectedTabIndex = estados.indexOf(selectedEstado).coerceAtLeast(0),
            containerColor = Color.Transparent,
            edgePadding = 0.dp,
            indicator = { },
            divider = { }
        ) {
            estados.forEach { estado ->
                val isSelected = estado == selectedEstado
                FilterChip(
                    selected = isSelected,
                    onClick = { onEstadoChange(estado) },
                    label = {
                        Text(
                            text = estado.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TCSBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Diálogo de detalle de solicitud de entrevista
 */
@Composable
private fun DetalleSolicitudEntrevistaDialog(
    solicitud: SolicitudReadDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detalle de Entrevista",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(label = "Estado", value = solicitud.estadoSolicitud)
                InfoRow(label = "Tipo", value = solicitud.tipoSolicitud)

                HorizontalDivider()

                solicitud.datosEntrevistaPropuesta?.let { entrevista ->
                    Text(
                        text = "Datos de la Entrevista",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    InfoRow(label = "Motivo", value = entrevista.motivo)
                    InfoRow(label = "Periodo", value = entrevista.periodo)
                    entrevista.fechaSugerida?.let {
                        InfoRow(label = "Fecha sugerida", value = it)
                    }
                }

                HorizontalDivider()

                InfoRow(label = "Fecha creación", value = solicitud.fechaCreacion.take(10))
                solicitud.fechaRevision?.let {
                    InfoRow(label = "Fecha revisión", value = it.take(10))
                }

                solicitud.observacionAdmin?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Observación del Administrador",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

/**
 * Diálogo para cambiar el estado de una solicitud
 */
@Composable
private fun CambioEstadoDialog(
    solicitud: SolicitudReadDto,
    onDismiss: () -> Unit,
    onConfirmar: (String, String?) -> Unit
) {
    var nuevoEstado by remember { mutableStateOf(solicitud.estadoSolicitud) }
    var observacion by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val estadosDisponibles = listOf(
        "PENDIENTE",
        "EN_REVISION",
        "PROGRAMADA",
        "APROBADA",
        "RECHAZADA",
        "ANULADA"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cambiar Estado",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Solicitud: ${solicitud.tipoSolicitud}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )

                Text(
                    text = "Estado actual: ${solicitud.estadoSolicitud}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                // Selector de nuevo estado
                Text(
                    text = "Nuevo estado",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                estadosDisponibles.forEach { estado ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = estado == nuevoEstado,
                            onClick = { nuevoEstado = estado }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = estado.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                HorizontalDivider()

                // Campo de observación
                OutlinedTextField(
                    value = observacion,
                    onValueChange = {
                        observacion = it
                        showError = false
                    },
                    label = {
                        Text(if (nuevoEstado == "RECHAZADA") "Observación *" else "Observación")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("La observación es obligatoria para rechazar") }
                    } else null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validar observación si es RECHAZADA
                    if (nuevoEstado == "RECHAZADA" && observacion.isBlank()) {
                        showError = true
                    } else {
                        onConfirmar(
                            nuevoEstado,
                            observacion.takeIf { it.isNotBlank() }
                        )
                    }
                }
            ) {
                Text("Confirmar", color = TCSBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF666666)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF333333)
        )
    }
}