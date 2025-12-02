package com.example.project_3_tcs_grupo4_dam

/**
 * EJEMPLOS DE USO - DASHBOARD DE NOTIFICACIONES UNIFICADO
 *
 * Este archivo contiene ejemplos prácticos de cómo usar el Dashboard de Notificaciones
 * en diferentes escenarios de la aplicación.
 */

// ============================================================================
// EJEMPLO 1: Usar el Dashboard en una Activity/Screen de Compose
// ============================================================================

/*
@Composable
fun MiPantallaNotificaciones(navController: NavController) {
    // La implementación completa está en NotificacionesDashboardScreen.kt
    NotificacionesDashboardScreen(navController)
}
*/

// ============================================================================
// EJEMPLO 2: Integrar el contador de notificaciones en un BottomNavBar
// ============================================================================

/*
@Composable
fun MiBottomNavBar(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )

    val unreadCount by viewModel.unreadCount.collectAsState()

    NavigationBar {
        NavigationBarItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge { Text(unreadCount.toString()) }
                        }
                    }
                ) {
                    Icon(Icons.Rounded.Notifications, "Notificaciones")
                }
            },
            label = { Text("Notificaciones") },
            selected = false,
            onClick = {
                navController.navigate("notificaciones_dashboard")
            }
        )
    }
}
*/

// ============================================================================
// EJEMPLO 3: Anunciar una vacante desde la pantalla de administración
// ============================================================================

/*
@Composable
fun VacanteDetalleAdminScreen(
    vacanteId: String,
    navController: NavController
) {
    val vacantesViewModel: VacantesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observar estados
    val isAnunciando by vacantesViewModel.isAnunciando.collectAsState()
    val successMessage by vacantesViewModel.successMessage.collectAsState()
    val errorMessage by vacantesViewModel.errorMessage.collectAsState()

    // Mostrar mensaje de éxito
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            vacantesViewModel.clearSuccessMessage()
        }
    }

    // Mostrar mensaje de error
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            vacantesViewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Información de la vacante...
            Text("Vacante: Senior Developer")
            Text("Skills requeridos: React, Node.js")

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de anuncio
            Button(
                onClick = {
                    vacantesViewModel.anunciarVacante(vacanteId)
                },
                enabled = !isAnunciando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isAnunciando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Anunciando...")
                } else {
                    Icon(Icons.Rounded.Send, "Anunciar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Anunciar Vacante a Colaboradores")
                }
            }

            Text(
                text = "Se enviará una notificación a todos los colaboradores que cumplan con los requisitos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
*/

// ============================================================================
// EJEMPLO 4: Cargar notificaciones manualmente desde cualquier parte
// ============================================================================

/*
class MiActivity : ComponentActivity() {
    private lateinit var viewModel: NotificacionesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotificacionesViewModel(sessionManager, applicationContext) as T
                }
            }
        )[NotificacionesViewModel::class.java]

        // Cargar notificaciones
        val esAdmin = sessionManager.getRol()?.equals("ADMIN", ignoreCase = true) ?: false
        val userId = sessionManager.getColaboradorId()

        lifecycleScope.launch {
            viewModel.cargarNotificaciones(esAdmin, userId)

            // Observar cambios
            viewModel.alertasDashboard.collect { alertas ->
                Log.d("MiActivity", "Se cargaron ${alertas.size} notificaciones")
            }
        }
    }
}
*/

// ============================================================================
// EJEMPLO 5: Uso del Repository directamente (sin ViewModel)
// ============================================================================

/*
class MiServicio {
    private val repository = NotificacionesRepository(RetrofitClient.alertasApi)

    suspend fun obtenerNotificacionesAdmin(): List<AlertaDashboard> {
        val result = repository.obtenerDashboard(esAdmin = true, userId = null)

        return result.getOrElse { error ->
            Log.e("MiServicio", "Error obteniendo notificaciones", error)
            emptyList()
        }
    }

    suspend fun anunciarVacanteNueva(vacanteId: String): Boolean {
        val result = repository.anunciarVacante(vacanteId)

        return result.isSuccess
    }
}
*/

// ============================================================================
// EJEMPLO 6: Personalizar el Card de Notificación
// ============================================================================

/*
@Composable
fun MiAlertaCardPersonalizada(alerta: AlertaDashboard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (alerta.colorPrioridad) {
                ColorPrioridad.ROJO -> Color(0xFFFFCDD2)
                ColorPrioridad.AMARILLO -> Color(0xFFFFF9C4)
                ColorPrioridad.VERDE -> Color(0xFFC8E6C9)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Icono según tipo
            Icon(
                imageVector = when (alerta.tipoOrigen) {
                    TipoOrigenAlerta.SKILL_GAP -> Icons.Rounded.TrendingUp
                    TipoOrigenAlerta.CERTIFICACION -> Icons.Rounded.School
                    TipoOrigenAlerta.VACANTE_DISPONIBLE -> Icons.Rounded.Work
                    TipoOrigenAlerta.GENERICA -> Icons.Rounded.Info
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = alerta.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = alerta.mensaje,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alerta.fecha,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
*/

// ============================================================================
// EJEMPLO 7: Filtrar notificaciones por tipo
// ============================================================================

/*
@Composable
fun NotificacionesFiltradasScreen() {
    val viewModel: NotificacionesViewModel = viewModel()
    val todasLasNotificaciones by viewModel.alertasDashboard.collectAsState()

    var filtroSeleccionado by remember { mutableStateOf<TipoOrigenAlerta?>(null) }

    val notificacionesFiltradas = remember(todasLasNotificaciones, filtroSeleccionado) {
        if (filtroSeleccionado == null) {
            todasLasNotificaciones
        } else {
            todasLasNotificaciones.filter { it.tipoOrigen == filtroSeleccionado }
        }
    }

    Column {
        // Filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtroSeleccionado == null,
                onClick = { filtroSeleccionado = null },
                label = { Text("Todas") }
            )
            FilterChip(
                selected = filtroSeleccionado == TipoOrigenAlerta.SKILL_GAP,
                onClick = { filtroSeleccionado = TipoOrigenAlerta.SKILL_GAP },
                label = { Text("Skills") }
            )
            FilterChip(
                selected = filtroSeleccionado == TipoOrigenAlerta.VACANTE_DISPONIBLE,
                onClick = { filtroSeleccionado = TipoOrigenAlerta.VACANTE_DISPONIBLE },
                label = { Text("Vacantes") }
            )
        }

        // Lista filtrada
        LazyColumn {
            items(notificacionesFiltradas) { alerta ->
                AlertaDashboardCard(alerta = alerta, onClick = {})
            }
        }
    }
}
*/

// ============================================================================
// EJEMPLO 8: Mostrar notificaciones en tiempo real con periodicidad
// ============================================================================

/*
@Composable
fun NotificacionesEnTiempoReal() {
    val viewModel: NotificacionesViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val esAdmin = sessionManager.getRol()?.equals("ADMIN", ignoreCase = true) ?: false
    val userId = sessionManager.getColaboradorId()

    // Recargar cada 30 segundos
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.cargarNotificaciones(esAdmin, userId)
            delay(30_000) // 30 segundos
        }
    }

    // UI normal...
}
*/

// ============================================================================
// EJEMPLO 9: JSON de Respuesta Esperado del Backend
// ============================================================================

/*
// GET /api/alertas/dashboard/admin
// Response esperado:
{
  "success": true,
  "message": "Dashboard obtenido correctamente",
  "data": [
    {
      "idReferencia": "alerta-001",
      "titulo": "Brecha crítica en React",
      "mensaje": "Tu nivel en React está 2 puntos por debajo del requerido para la vacante Senior Frontend",
      "fecha": "2025-12-02T10:30:00Z",
      "tipoOrigen": "SKILL_GAP",
      "activa": true,
      "colorPrioridad": "ROJO"
    },
    {
      "idReferencia": "alerta-002",
      "titulo": "Nueva vacante disponible",
      "mensaje": "Hay una nueva vacante de Backend Developer que coincide con tu perfil",
      "fecha": "2025-12-02T09:15:00Z",
      "tipoOrigen": "VACANTE_DISPONIBLE",
      "activa": true,
      "colorPrioridad": "VERDE"
    },
    {
      "idReferencia": "alerta-003",
      "titulo": "Certificación AWS próxima a vencer",
      "mensaje": "Tu certificación AWS Solutions Architect vence en 15 días",
      "fecha": "2025-12-01T14:20:00Z",
      "tipoOrigen": "CERTIFICACION",
      "activa": false,
      "colorPrioridad": "AMARILLO"
    }
  ]
}

// POST /api/alertas/anunciar-vacante
// Request:
{
  "vacanteId": "vacante-456"
}

// Response:
{
  "success": true,
  "message": "Vacante anunciada a 15 colaboradores elegibles",
  "data": null
}
*/

