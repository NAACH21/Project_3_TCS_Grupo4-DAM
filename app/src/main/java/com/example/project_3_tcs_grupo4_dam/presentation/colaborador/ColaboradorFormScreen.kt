package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 🔹 Color Corporativo TCS
private val PrimaryBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorFormScreen(navController: NavController) {
    val viewModel: ColaboradorFormViewModel = viewModel()

    // Observar estados
    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val area by viewModel.area.collectAsState()
    val rolLaboral by viewModel.rolLaboral.collectAsState()
    val disponible by viewModel.disponibleParaMovilidad.collectAsState()

    // Catálogos
    val areasCatalogo by viewModel.areas.collectAsState()
    val rolesCatalogo by viewModel.rolesLaborales.collectAsState()
    val tiposSkillCatalogo by viewModel.tiposSkill.collectAsState()
    val nivelesSkillCatalogo by viewModel.nivelesSkill.collectAsState()

    // Estado del colaborador
    val estadoColaborador by viewModel.estado.collectAsState()

    // Estados del diálogo de selección de skills
    val showSkillPickerDialog by viewModel.showSkillPickerDialog.collectAsState()
    val selectedTipoSkill by viewModel.selectedTipoSkill.collectAsState()
    val skillSearchText by viewModel.skillSearchText.collectAsState()
    val filteredSkillSuggestions by viewModel.filteredSkillSuggestions.collectAsState()

    val skills by viewModel.skills.collectAsState()
    val certificaciones by viewModel.certificaciones.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Estado de upload de PDFs
    val isUploadingCertificaciones by viewModel.isUploadingCertificaciones.collectAsState()
    val isUploadingAnyCert = isUploadingCertificaciones.any { it }

    // Navegar de vuelta al guardar exitosamente
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navController.popBackStack()
        }
    }

    // Snackbar para errores
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showAddPdfNameDialog by remember { mutableStateOf(false) }
    var newPdfName by remember { mutableStateOf("") }

    // Estados para Date Pickers de certificaciones
    var showDatePickerFechaObtencion by remember { mutableStateOf(false) }
    var showDatePickerFechaVencimiento by remember { mutableStateOf(false) }
    var selectedCertIndexForDatePicker by remember { mutableStateOf<Int?>(null) }

    // Context y file picker para PDFs
    val context = LocalContext.current
    var selectedCertIndexForUpload by remember { mutableStateOf<Int?>(null) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File(context.cacheDir, "cert_${System.currentTimeMillis()}.pdf")
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Llamar al ViewModel si tenemos un índice capturado
                    selectedCertIndexForUpload?.let { index ->
                        viewModel.uploadCertificacionPdf(index, tempFile)
                        selectedCertIndexForUpload = null
                    }
                } catch (e: Exception) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Error al procesar archivo: ${e.message}")
                    }
                }
            }
        }
    )

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    val titulo = if (viewModel.isEditMode) "Editar Colaborador" else "Nuevo Colaborador"
    val textoBoton = if (viewModel.isEditMode) "Guardar cambios" else "Guardar colaborador"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        titulo, 
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección 1: Datos Personales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "1. Datos personales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        OutlinedTextField(
                            value = nombres,
                            onValueChange = viewModel::onNombresChange,
                            label = { Text("Nombres *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                cursorColor = PrimaryBlue
                            )
                        )

                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = viewModel::onApellidosChange,
                            label = { Text("Apellidos *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                cursorColor = PrimaryBlue
                            )
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = viewModel::onCorreoChange,
                            label = { Text("Correo *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                cursorColor = PrimaryBlue
                            )
                        )

                        // Dropdown para Área
                        var expandedArea by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedArea,
                            onExpandedChange = { expandedArea = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = area,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Área *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    focusedLabelColor = PrimaryBlue,
                                    cursorColor = PrimaryBlue
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedArea,
                                onDismissRequest = { expandedArea = false }
                            ) {
                                areasCatalogo.forEach { areaItem ->
                                    DropdownMenuItem(
                                        text = { Text(areaItem) },
                                        onClick = {
                                            viewModel.onAreaChange(areaItem)
                                            expandedArea = false
                                        }
                                    )
                                }
                            }
                        }

                        // Dropdown para Rol laboral
                        var expandedRol by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedRol,
                            onExpandedChange = { expandedRol = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = rolLaboral,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Rol laboral *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    focusedLabelColor = PrimaryBlue,
                                    cursorColor = PrimaryBlue
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRol,
                                onDismissRequest = { expandedRol = false }
                            ) {
                                rolesCatalogo.forEach { rolItem ->
                                    DropdownMenuItem(
                                        text = { Text(rolItem) },
                                        onClick = {
                                            viewModel.onRolLaboralChange(rolItem)
                                            expandedRol = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Disponible para movilidad")
                            Switch(
                                checked = disponible,
                                onCheckedChange = viewModel::onDisponibleParaMovilidadChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryBlue,
                                    checkedTrackColor = PrimaryBlue.copy(alpha = 0.5f)
                                )
                            )
                        }

                        // Dropdown de Estado - Solo visible en modo edición
                        if (viewModel.isEditMode) {
                            var expandedEstado by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedEstado,
                                onExpandedChange = { expandedEstado = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = estadoColaborador,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Estado") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedEstado
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(
                                            MenuAnchorType.PrimaryNotEditable,
                                            enabled = true
                                        ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = if (estadoColaborador == "ACTIVO")
                                            Color(0xFFE8F5E9)
                                        else
                                            Color(0xFFFFEBEE),
                                        focusedBorderColor = PrimaryBlue
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedEstado,
                                    onDismissRequest = { expandedEstado = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("ACTIVO") },
                                        onClick = {
                                            viewModel.onEstadoChange("ACTIVO")
                                            expandedEstado = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("INACTIVO") },
                                        onClick = {
                                            viewModel.onEstadoChange("INACTIVO")
                                            expandedEstado = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección 2: Skills (editable lista embebida)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "2. Skills",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        // Botón para agregar skills con validación: debe haber al menos 1 cert con PDF
                        val tieneCertConPdf =
                            certificaciones.any { !it.archivoPdfUrl.isNullOrBlank() }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!tieneCertConPdf) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Debes subir al menos un certificado en PDF antes de agregar skills")
                                        }
                                    } else {
                                        viewModel.openSkillPicker()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = tieneCertConPdf,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                )
                            ) {
                                Text(text = "+ Agregar skill")
                            }

                            // Tooltip con icono de advertencia cuando no hay certificaciones con PDF
                            if (!tieneCertConPdf) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer.copy(
                                                alpha = 0.3f
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Advertencia",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Agrega y sube primero al menos una certificación (PDF) para poder registrar skills.",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (skills.isEmpty()) {
                            Text(
                                text = "Sin skills agregados",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            skills.forEachIndexed { index, skill ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F5F5)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Header con título y botón eliminar
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Skill ${index + 1}",
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            IconButton(onClick = { viewModel.removeSkill(index) }) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Eliminar"
                                                )
                                            }
                                        }

                                        // Campo Nombre
                                        OutlinedTextField(
                                            value = skill.nombre,
                                            onValueChange = {
                                                viewModel.updateSkillNombre(
                                                    index,
                                                    it
                                                )
                                            },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                        )

                                        // Tipo (Dropdown desde catálogo)
                                        var expandedTipo by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                            expanded = expandedTipo,
                                            onExpandedChange = { expandedTipo = it },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = skill.tipo,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Tipo") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expandedTipo
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(
                                                        MenuAnchorType.PrimaryNotEditable,
                                                        enabled = true
                                                    ),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expandedTipo,
                                                onDismissRequest = { expandedTipo = false }
                                            ) {
                                                tiposSkillCatalogo.forEach { tipo ->
                                                    DropdownMenuItem(
                                                        text = { Text(tipo) },
                                                        onClick = {
                                                            viewModel.updateSkillTipo(index, tipo)
                                                            expandedTipo = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        // Nivel (Dropdown desde catálogo con descripción)
                                        var expandedNivel by remember { mutableStateOf(false) }
                                        val nivelActual =
                                            nivelesSkillCatalogo.find { it.codigo == skill.nivel }
                                        val nivelTexto =
                                            nivelActual?.let { "Nivel ${it.codigo} - ${it.descripcion}" }
                                                ?: "Nivel ${skill.nivel}"

                                        ExposedDropdownMenuBox(
                                            expanded = expandedNivel,
                                            onExpandedChange = { expandedNivel = it },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = nivelTexto,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Nivel") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expandedNivel
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(
                                                        MenuAnchorType.PrimaryNotEditable,
                                                        enabled = true
                                                    ),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expandedNivel,
                                                onDismissRequest = { expandedNivel = false }
                                            ) {
                                                nivelesSkillCatalogo.forEach { nivel ->
                                                    DropdownMenuItem(
                                                        text = { Text("Nivel ${nivel.codigo} - ${nivel.descripcion}") },
                                                        onClick = {
                                                            viewModel.updateSkillNivel(
                                                                index,
                                                                nivel.codigo
                                                            )
                                                            expandedNivel = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        // Switch para Crítico
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Skill crítico",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Switch(
                                                checked = skill.esCritico,
                                                onCheckedChange = {
                                                    viewModel.updateSkillEsCritico(
                                                        index,
                                                        it
                                                    )
                                                },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = PrimaryBlue,
                                                    checkedTrackColor = PrimaryBlue.copy(alpha = 0.5f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sección 3: Certificaciones
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "3. Certificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.addCertificacion() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text(text = "+ Agregar certificación")
                            }

                            OutlinedButton(
                                onClick = { showAddPdfNameDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                            ) {
                                Text(text = "Agregar certificación (solo nombre PDF)")
                            }
                        }

                        if (certificaciones.isEmpty()) {
                            Text(
                                text = "Sin certificaciones",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            certificaciones.forEachIndexed { index, cert ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Certificación ${index + 1}",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            IconButton(onClick = {
                                                viewModel.removeCertificacion(
                                                    index
                                                )
                                            }) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Eliminar"
                                                )
                                            }
                                        }

                                        OutlinedTextField(
                                            value = cert.nombre,
                                            onValueChange = {
                                                viewModel.updateCertificacionNombre(
                                                    index,
                                                    it
                                                )
                                            },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                        )

                                        OutlinedTextField(
                                            value = cert.institucion,
                                            onValueChange = {
                                                viewModel.updateCertificacionInstitucion(
                                                    index,
                                                    it
                                                )
                                            },
                                            label = { Text("Institución") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                        )

                                        // Fecha de obtención con Date Picker
                                        OutlinedTextField(
                                            value = cert.fechaObtencion ?: "",
                                            onValueChange = { },
                                            label = { Text("Fecha de obtención") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCertIndexForDatePicker = index
                                                    showDatePickerFechaObtencion = true
                                                },
                                            enabled = false,
                                            readOnly = true,
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    selectedCertIndexForDatePicker = index
                                                    showDatePickerFechaObtencion = true
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.DateRange,
                                                        contentDescription = "Seleccionar fecha"
                                                    )
                                                }
                                            },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )

                                        // Fecha de vencimiento con Date Picker
                                        OutlinedTextField(
                                            value = cert.fechaVencimiento ?: "",
                                            onValueChange = { },
                                            label = { Text("Fecha de vencimiento") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCertIndexForDatePicker = index
                                                    showDatePickerFechaVencimiento = true
                                                },
                                            enabled = false,
                                            readOnly = true,
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    selectedCertIndexForDatePicker = index
                                                    showDatePickerFechaVencimiento = true
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.DateRange,
                                                        contentDescription = "Seleccionar fecha"
                                                    )
                                                }
                                            },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )

                                        // Sección de carga de PDF - Botón para upload
                                        Button(
                                            onClick = {
                                                selectedCertIndexForUpload = index
                                                pdfPickerLauncher.launch("application/pdf")
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = (isUploadingCertificaciones.getOrNull(index)
                                                ?: false).not(),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = PrimaryBlue)
                                        ) {
                                            if (isUploadingCertificaciones.getOrNull(index) == true) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = PrimaryBlue,
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Subiendo...")
                                            } else {
                                                Icon(
                                                    Icons.Default.Upload,
                                                    contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Subir certificado (PDF)")
                                            }
                                        }

                                        // Estado del archivo PDF
                                        val statusText = if (cert.archivoPdfUrl.isNullOrBlank()) {
                                            "Sin archivo subido"
                                        } else {
                                            "✓ Archivo subido"
                                        }
                                        Text(
                                            text = statusText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (cert.archivoPdfUrl.isNullOrBlank()) {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            } else {
                                                PrimaryBlue
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Botón Guardar
                        Button(
                            onClick = { viewModel.guardarColaborador() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving && !isUploadingAnyCert,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (isSaving || isUploadingAnyCert) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isSaving) "Guardando..." else textoBoton)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Diálogo de selección de skills
                if (showSkillPickerDialog) {
                    SkillPickerDialog(
                        selectedTipo = selectedTipoSkill,
                        tiposDisponibles = tiposSkillCatalogo,
                        searchText = skillSearchText,
                        suggestions = filteredSkillSuggestions,
                        onTipoChange = viewModel::onTipoSkillSelected,
                        onSearchTextChange = viewModel::onSkillSearchTextChange,
                        onSkillClick = viewModel::onSkillSuggestionClick,
                        onDismiss = viewModel::closeSkillPicker
                    )
                }

                if (showAddPdfNameDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddPdfNameDialog = false },
                        title = { Text("Agregar certificación (nombre PDF)") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newPdfName,
                                    onValueChange = { newPdfName = it },
                                    label = { Text("Nombre del PDF") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                )
                                Text(
                                    text = "Ej: certificado_reto.pdf (solo nombre o URL corta)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (newPdfName.isNotBlank()) {
                                    viewModel.addCertificacionConNombre(newPdfName.trim())
                                    newPdfName = ""
                                    showAddPdfNameDialog = false
                                } else {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Ingresa el nombre del PDF") }
                                }
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) { Text("Agregar") }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showAddPdfNameDialog = false
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) { Text("Cancelar") }
                        }
                    )
                }

                // Date Picker para Fecha de Obtención
                if (showDatePickerFechaObtencion && selectedCertIndexForDatePicker != null) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePickerFechaObtencion = false
                            selectedCertIndexForDatePicker = null
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val dateString = formatter.format(Date(millis))
                                    viewModel.updateCertificacionFechaObtencion(
                                        selectedCertIndexForDatePicker!!,
                                        dateString
                                    )
                                }
                                showDatePickerFechaObtencion = false
                                selectedCertIndexForDatePicker = null
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDatePickerFechaObtencion = false
                                selectedCertIndexForDatePicker = null
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = {
                                Text(
                                    text = "Fecha de obtención",
                                    modifier = Modifier.padding(16.dp)
                                )
                            },
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = PrimaryBlue,
                                todayDateBorderColor = PrimaryBlue,
                                todayContentColor = PrimaryBlue
                            )
                        )
                    }
                }

                // Date Picker para Fecha de Vencimiento
                if (showDatePickerFechaVencimiento && selectedCertIndexForDatePicker != null) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePickerFechaVencimiento = false
                            selectedCertIndexForDatePicker = null
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val dateString = formatter.format(Date(millis))
                                    viewModel.updateCertificacionFechaVencimiento(
                                        selectedCertIndexForDatePicker!!,
                                        dateString
                                    )
                                }
                                showDatePickerFechaVencimiento = false
                                selectedCertIndexForDatePicker = null
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDatePickerFechaVencimiento = false
                                selectedCertIndexForDatePicker = null
                            }, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = {
                                Text(
                                    text = "Fecha de vencimiento",
                                    modifier = Modifier.padding(16.dp)
                                )
                            },
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = PrimaryBlue,
                                todayDateBorderColor = PrimaryBlue,
                                todayContentColor = PrimaryBlue
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo para seleccionar un skill del catálogo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillPickerDialog(
    selectedTipo: String,
    tiposDisponibles: List<String>,
    searchText: String,
    suggestions: List<com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.SkillCatalogItemDto>,
    onTipoChange: (String) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSkillClick: (com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.SkillCatalogItemDto) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Skill",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector de tipo de skill
                Text(
                    text = "Tipo de skill",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                var expandedTipo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedTipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        tiposDisponibles.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    onTipoChange(tipo)
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }

                // Buscador
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    label = { Text("Buscar skill...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Escribe para filtrar") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                // Lista de sugerencias
                Text(
                    text = "Skills disponibles (${suggestions.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (suggestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron skills",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(suggestions) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSkillClick(item) },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.nombre,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = item.tipo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)) {
                Text("Cancelar")
            }
        }
    )
}
