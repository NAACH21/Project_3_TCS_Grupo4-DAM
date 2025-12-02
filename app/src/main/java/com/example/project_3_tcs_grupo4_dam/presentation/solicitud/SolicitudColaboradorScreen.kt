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
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud.*
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

private val TCSBlue = Color(0xFF00549F)
private val LightGrayBg = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudColaboradorScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // ViewModel de solicitudes
    val viewModel: SolicitudColaboradorViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SolicitudColaboradorViewModel(
                    solicitudesRepository = SolicitudesRepositoryImpl(RetrofitClient.solicitudesApi),
                    catalogoRepository = CatalogoRepositoryImpl(),
                    sessionManager = sessionManager
                ) as T
            }
        }
    )

    // ViewModel de notificaciones para el badge
    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T   // ✅ ARREGLADO
            }
        }
    )


    // Estados del ViewModel
    val solicitudes by viewModel.solicitudesFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val filtroTipo by viewModel.filtroTipo.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val isDialogOpen by viewModel.isDialogNuevaSolicitudOpen.collectAsState()
    val showDetalleDialog by viewModel.showDetalleDialog.collectAsState()
    val solicitudSeleccionada by viewModel.solicitudSeleccionada.collectAsState()
    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()   // ✅ CORRECTO

    val snackbarHostState = remember { SnackbarHostState() }

    // DEBUG: Log del estado de solicitudes
    LaunchedEffect(solicitudes, isLoading) {
        android.util.Log.d("SolicitudScreen", "=== ESTADO DE LA UI ===")
        android.util.Log.d("SolicitudScreen", "isLoading: $isLoading")
        android.util.Log.d("SolicitudScreen", "solicitudes.size: ${solicitudes.size}")
        android.util.Log.d("SolicitudScreen", "solicitudes.isEmpty(): ${solicitudes.isEmpty()}")
        solicitudes.forEachIndexed { index, sol ->
            android.util.Log.d("SolicitudScreen", "  [$index] ${sol.tipoSolicitudGeneral} - ${sol.estadoSolicitud}")
        }
    }

    // Recargar solicitudes cada vez que se vuelve a la pantalla
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry) {
        android.util.Log.d("SolicitudScreen", "LaunchedEffect - Recargando solicitudes")
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
                        text = "Mis Solicitudes",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            ColaboradorBottomNavBar(
                navController = navController,
                alertCount = unreadCount
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si no hay error de rol
            if (errorMessage != "Esta pantalla es solo para colaboradores") {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.abrirNuevaSolicitud() },
                    containerColor = TCSBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Solicitud")
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
            // Verificar si hay error de rol antes de mostrar contenido
            if (errorMessage == "Esta pantalla es solo para colaboradores") {
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
                            text = "Esta pantalla es solo para colaboradores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Barra de filtros - Solo muestra Certificación y Actualización de Skills
                SolicitudFilterBar(
                    selectedTipo = filtroTipo,
                    onTipoChange = { viewModel.aplicarFiltroTipo(it) },
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
                            mensaje = "Aún no tienes solicitudes",
                            submensaje = "Crea tu primera solicitud de certificación o actualización de skills"
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
                                    onAnularClick = { solicitudId ->
                                        // ⭐ Conectar con el ViewModel para anular
                                        viewModel.anularSolicitud(solicitudId)
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

    // Bottom Sheet para SELECCIONAR tipo de solicitud
    if (isDialogOpen) {
        NuevaSolicitudBottomSheet(
            onDismiss = { viewModel.cerrarNuevaSolicitud() },
            onSeleccionarNuevaSkill = {
                // Navegar a pantalla de NUEVA SKILL con certificado
                navController.navigate(Routes.SOLICITUD_SKILLS_COLABORADOR + "?esNueva=true")
            },
            onSeleccionarActualizarSkill = {
                // Navegar a pantalla de ACTUALIZAR SKILL existente con certificado
                navController.navigate(Routes.SOLICITUD_SKILLS_COLABORADOR + "?esNueva=false")
            }
        )
    }

    // Diálogo de detalle de solicitud
    if (showDetalleDialog && solicitudSeleccionada != null) {
        DetalleSolicitudDialog(
            solicitud = solicitudSeleccionada!!,
            onDismiss = { viewModel.cerrarDetalleSolicitud() }
        )
    }
}

@Composable
private fun DetalleSolicitudDialog(
    solicitud: SolicitudReadDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detalle de Solicitud",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tipo y Estado
                InfoRow(label = "Tipo", value = getTipoLabel(solicitud.tipoSolicitudGeneral))
                InfoRow(label = "Estado", value = solicitud.estadoSolicitud)
                InfoRow(label = "Subtipo", value = solicitud.tipoSolicitud)

                Divider()

                // Información específica según tipo
                when (solicitud.tipoSolicitudGeneral) {
                    "CERTIFICACION" -> {
                        solicitud.certificacionPropuesta?.let { cert ->
                            Text(
                                text = "Certificación Propuesta",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            InfoRow(label = "Nombre", value = cert.nombre)
                            InfoRow(label = "Institución", value = cert.institucion)
                            cert.fechaObtencion?.let { InfoRow(label = "Fecha obtención", value = it) }
                            cert.fechaVencimiento?.let { InfoRow(label = "Fecha vencimiento", value = it) }
                        }
                    }

                    "ACTUALIZACION_SKILLS" -> {
                        solicitud.cambiosSkillsPropuestos?.let { cambios ->
                            Text(
                                text = "Cambios Propuestos (${cambios.size})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            cambios.forEach { cambio ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F7FA)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = cambio.nombre,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Tipo: ${cambio.tipo}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Nivel propuesto: ${cambio.nivelPropuesto}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Crítico: ${if (cambio.esCriticoPropuesto) "Sí" else "No"}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        cambio.motivo?.let {
                                            Text(
                                                text = "Motivo: $it",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "ENTREVISTA_DESEMPENO" -> {
                        solicitud.datosEntrevistaPropuesta?.let { entrevista ->
                            Text(
                                text = "Entrevista de Desempeño",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            InfoRow(label = "Periodo", value = entrevista.periodo)
                            InfoRow(label = "Motivo", value = entrevista.motivo)
                            entrevista.fechaSugerida?.let {
                                InfoRow(label = "Fecha sugerida", value = it)
                            }
                        }
                    }
                }

                Divider()

                // Fechas y observaciones
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

private fun getTipoLabel(tipo: String): String {
    return when (tipo) {
        "CERTIFICACION" -> "Certificación"
        "ACTUALIZACION_SKILLS" -> "Actualización de Skills"
        "ENTREVISTA_DESEMPENO" -> "Entrevista de Desempeño"
        else -> tipo
    }
}
