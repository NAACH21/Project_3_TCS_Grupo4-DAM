package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepositoryImpl
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TCSBlue = Color(0xFF00549F)

/**
 * Pantalla para crear solicitudes de ACTUALIZACION_SKILLS
 * Permite actualizar skill existente o crear skill nueva, SIEMPRE con certificado PDF
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudActualizacionSkillsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Obtener parámetro de navegación para saber si es nueva skill o actualización
    val navBackStackEntry = navController.currentBackStackEntry
    val esNueva = navBackStackEntry?.arguments?.getBoolean("esNueva") ?: true

    val viewModel: SolicitudActualizacionSkillsViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SolicitudActualizacionSkillsViewModel(
                    solicitudesRepository = SolicitudesRepositoryImpl(RetrofitClient.solicitudesApi),
                    certificadosRepository = CertificadosRepositoryImpl(context.applicationContext),
                    colaboradorRepository = ColaboradorRepositoryImpl(),
                    catalogoRepository = CatalogoRepositoryImpl(),
                    sessionManager = sessionManager
                ) as T
            }
        }
    )

    // Configurar el modo inicial según el parámetro de navegación
    LaunchedEffect(esNueva) {
        viewModel.onTipoSkillChange(esNueva)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val misSkills by viewModel.misSkillsActuales.collectAsState()
    val skillsCatalogo by viewModel.skillsCatalogo.collectAsState()
    val nivelesSkill by viewModel.nivelesSkill.collectAsState()

    val esSkillNueva by viewModel.esSkillNueva.collectAsState()
    val skillSeleccionada by viewModel.skillSeleccionada.collectAsState()
    val nombreSkillNueva by viewModel.nombreSkillNueva.collectAsState()
    val tipoSkillNueva by viewModel.tipoSkillNueva.collectAsState()
    val nivelPropuesto by viewModel.nivelPropuesto.collectAsState()
    val esCriticoPropuesto by viewModel.esCriticoPropuesto.collectAsState()
    val motivo by viewModel.motivo.collectAsState()

    val nombreCertificacion by viewModel.nombreCertificacion.collectAsState()
    val institucionCertificacion by viewModel.institucionCertificacion.collectAsState()
    val fechaObtencion by viewModel.fechaObtencion.collectAsState()
    val fechaVencimiento by viewModel.fechaVencimiento.collectAsState()
    val pdfSeleccionado by viewModel.pdfSeleccionado.collectAsState()
    val pdfSubiendo by viewModel.pdfSubiendo.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Estados para dropdowns
    var expandedSkillExistente by remember { mutableStateOf(false) }
    var expandedSkillCatalogo by remember { mutableStateOf(false) }
    var expandedTipoSkill by remember { mutableStateOf(false) }
    var expandedNivel by remember { mutableStateOf(false) }

    // Estados para DatePickers
    var showDatePickerObtencion by remember { mutableStateOf(false) }
    var showDatePickerVencimiento by remember { mutableStateOf(false) }

    // File picker para PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File(context.cacheDir, "cert_${System.currentTimeMillis()}.pdf")
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    viewModel.onPdfSeleccionado(tempFile)
                } catch (e: Exception) {
                    viewModel.onError("Error al procesar archivo: ${e.message}")
                }
            }
        }
    )

    // Mostrar mensajes
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (esSkillNueva) "Nueva Skill" else "Actualizar Skill")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner informativo del tipo de solicitud
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (esSkillNueva) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (esSkillNueva) Icons.Default.AddCircle else Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (esSkillNueva) TCSBlue else Color(0xFF4CAF50),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (esSkillNueva) "Nueva Skill" else "Actualizar Skill",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (esSkillNueva)
                                    "Agrega una nueva skill con certificado de respaldo"
                                else
                                    "Actualiza el nivel de una skill existente",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                // Sección: Skill
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Datos de Skill",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (!esSkillNueva) {
                            // Actualizar existente: Dropdown de mis skills
                            ExposedDropdownMenuBox(
                                expanded = expandedSkillExistente,
                                onExpandedChange = { expandedSkillExistente = it }
                            ) {
                                OutlinedTextField(
                                    value = skillSeleccionada?.nombre ?: "Selecciona una skill",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Skill actual") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSkillExistente) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedSkillExistente,
                                    onDismissRequest = { expandedSkillExistente = false }
                                ) {
                                    misSkills.forEach { skill ->
                                        DropdownMenuItem(
                                            text = { Text("${skill.nombre} (Nivel ${skill.nivel})") },
                                            onClick = {
                                                viewModel.onSkillExistenteSeleccionada(skill)
                                                expandedSkillExistente = false
                                            }
                                        )
                                    }
                                }
                            }

                            skillSeleccionada?.let { skill ->
                                Text(
                                    "Nivel actual: ${skill.nivel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    "Crítico actual: ${if (skill.esCritico) "Sí" else "No"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            // Skill nueva: Buscar en catálogo
                            ExposedDropdownMenuBox(
                                expanded = expandedSkillCatalogo,
                                onExpandedChange = { expandedSkillCatalogo = it }
                            ) {
                                OutlinedTextField(
                                    value = nombreSkillNueva,
                                    onValueChange = { viewModel.onNombreSkillNuevaChange(it) },
                                    label = { Text("Nombre de la skill nueva") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSkillCatalogo) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                )
                                if (nombreSkillNueva.length >= 2) {
                                    val filtradas = skillsCatalogo.filter {
                                        it.nombre.contains(nombreSkillNueva, ignoreCase = true)
                                    }
                                    if (filtradas.isNotEmpty()) {
                                        ExposedDropdownMenu(
                                            expanded = true,
                                            onDismissRequest = { expandedSkillCatalogo = false }
                                        ) {
                                            filtradas.forEach { skillCatalog ->
                                                DropdownMenuItem(
                                                    text = { Text("${skillCatalog.nombre} (${skillCatalog.tipo})") },
                                                    onClick = {
                                                        viewModel.onSkillCatalogoSeleccionada(skillCatalog)
                                                        expandedSkillCatalogo = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Tipo de skill (Técnica/Blanda)
                            ExposedDropdownMenuBox(
                                expanded = expandedTipoSkill,
                                onExpandedChange = { expandedTipoSkill = it }
                            ) {
                                OutlinedTextField(
                                    value = tipoSkillNueva,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tipo de skill") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoSkill) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedTipoSkill,
                                    onDismissRequest = { expandedTipoSkill = false }
                                ) {
                                    listOf("Técnica", "Blanda").forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo) },
                                            onClick = {
                                                viewModel.onTipoSkillNuevaChange(tipo)
                                                expandedTipoSkill = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Nivel propuesto
                        ExposedDropdownMenuBox(
                            expanded = expandedNivel,
                            onExpandedChange = { expandedNivel = it }
                        ) {
                            OutlinedTextField(
                                value = "Nivel $nivelPropuesto",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nivel propuesto") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNivel) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedNivel,
                                onDismissRequest = { expandedNivel = false }
                            ) {
                                (1..5).forEach { nivel ->
                                    DropdownMenuItem(
                                        text = { Text("Nivel $nivel") },
                                        onClick = {
                                            viewModel.onNivelPropuestoChange(nivel)
                                            expandedNivel = false
                                        }
                                    )
                                }
                            }
                        }

                        // Es crítico propuesto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("¿Es skill crítica?")
                            Switch(
                                checked = esCriticoPropuesto,
                                onCheckedChange = { viewModel.onEsCriticoChange(it) }
                            )
                        }

                        // Motivo
                        OutlinedTextField(
                            value = motivo,
                            onValueChange = { viewModel.onMotivoChange(it) },
                            label = { Text("Motivo (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }

                // Sección: Certificado
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Certificado de Respaldo (Obligatorio)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )

                        OutlinedTextField(
                            value = nombreCertificacion,
                            onValueChange = { viewModel.onNombreCertificacionChange(it) },
                            label = { Text("Nombre del certificado") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = institucionCertificacion,
                            onValueChange = { viewModel.onInstitucionCertificacionChange(it) },
                            label = { Text("Institución") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Fecha obtención
                        OutlinedTextField(
                            value = fechaObtencion ?: "",
                            onValueChange = {},
                            label = { Text("Fecha de obtención (opcional)") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePickerObtencion = true }) {
                                    Icon(Icons.Default.DateRange, "Seleccionar fecha")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Fecha vencimiento
                        OutlinedTextField(
                            value = fechaVencimiento ?: "",
                            onValueChange = {},
                            label = { Text("Fecha de vencimiento (opcional)") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePickerVencimiento = true }) {
                                    Icon(Icons.Default.DateRange, "Seleccionar fecha")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botón subir PDF
                        Button(
                            onClick = { pdfPickerLauncher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !pdfSubiendo
                        ) {
                            if (pdfSubiendo) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Subiendo...")
                            } else {
                                Icon(Icons.Default.AttachFile, null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (pdfSeleccionado == null) "Seleccionar PDF" else "Cambiar PDF")
                            }
                        }

                        pdfSeleccionado?.let {
                            Text(
                                "✓ PDF subido: ${it.name}",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Botón enviar
                Button(
                    onClick = { viewModel.enviarSolicitud() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !pdfSubiendo
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Creando solicitud...")
                    } else {
                        Text("Enviar Solicitud")
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // DatePickers
    if (showDatePickerObtencion) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerObtencion = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.onFechaObtencionChange(formatter.format(Date(millis)))
                    }
                    showDatePickerObtencion = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerObtencion = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDatePickerVencimiento) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerVencimiento = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.onFechaVencimientoChange(formatter.format(Date(millis)))
                    }
                    showDatePickerVencimiento = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerVencimiento = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

