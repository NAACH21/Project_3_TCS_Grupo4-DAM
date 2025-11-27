package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionCreateDto

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

    // Navegar de vuelta al guardar exitosamente
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navController.popBackStack()
        }
    }

    // Snackbar para errores
    val snackbarHostState = remember { SnackbarHostState() }

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
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
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
                CircularProgressIndicator()
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = nombres,
                            onValueChange = viewModel::onNombresChange,
                            label = { Text("Nombres *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = viewModel::onApellidosChange,
                            label = { Text("Apellidos *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = viewModel::onCorreoChange,
                            label = { Text("Correo *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
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
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                            Switch(checked = disponible, onCheckedChange = viewModel::onDisponibleParaMovilidadChange)
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
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = if (estadoColaborador == "ACTIVO")
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        else
                                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            fontWeight = FontWeight.Bold
                        )

                        Button(onClick = { viewModel.openSkillPicker() }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "+ Agregar skill")
                        }

                        if (skills.isEmpty()) {
                            Text(text = "Sin skills agregados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            skills.forEachIndexed { index, skill ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                                Icon(Icons.Default.Close, contentDescription = "Eliminar")
                                            }
                                        }

                                        // Campo Nombre
                                        OutlinedTextField(
                                            value = skill.nombre,
                                            onValueChange = { viewModel.updateSkillNombre(index, it) },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
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
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                                        val nivelActual = nivelesSkillCatalogo.find { it.codigo == skill.nivel }
                                        val nivelTexto = nivelActual?.let { "Nivel ${it.codigo} - ${it.descripcion}" } ?: "Nivel ${skill.nivel}"

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
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNivel) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expandedNivel,
                                                onDismissRequest = { expandedNivel = false }
                                            ) {
                                                nivelesSkillCatalogo.forEach { nivel ->
                                                    DropdownMenuItem(
                                                        text = { Text("Nivel ${nivel.codigo} - ${nivel.descripcion}") },
                                                        onClick = {
                                                            viewModel.updateSkillNivel(index, nivel.codigo)
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
                                                onCheckedChange = { viewModel.updateSkillEsCritico(index, it) }
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            fontWeight = FontWeight.Bold
                        )

                        Button(onClick = { viewModel.addCertificacion() }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "+ Agregar certificación")
                        }

                        if (certificaciones.isEmpty()) {
                            Text(text = "Sin certificaciones", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            certificaciones.forEachIndexed { index, cert ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "Certificación ${index + 1}", fontWeight = FontWeight.SemiBold)
                                            IconButton(onClick = { viewModel.removeCertificacion(index) }) {
                                                Icon(Icons.Default.Close, contentDescription = "Eliminar")
                                            }
                                        }

                                        OutlinedTextField(
                                            value = cert.nombre,
                                            onValueChange = { viewModel.updateCertificacionNombre(index, it) },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cert.institucion,
                                            onValueChange = { viewModel.updateCertificacionInstitucion(index, it) },
                                            label = { Text("Institución") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cert.fechaObtencion ?: "",
                                            onValueChange = { viewModel.updateCertificacionFechaObtencion(index, it.ifBlank { null }) },
                                            label = { Text("Fecha de obtención (ISO)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cert.fechaVencimiento ?: "",
                                            onValueChange = { viewModel.updateCertificacionFechaVencimiento(index, it.ifBlank { null }) },
                                            label = { Text("Fecha de vencimiento (ISO)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cert.archivoPdfUrl ?: "",
                                            onValueChange = { viewModel.updateCertificacionArchivoUrl(index, it.ifBlank { null }) },
                                            label = { Text("Archivo PDF URL") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón Guardar
                Button(
                    onClick = { viewModel.guardarColaborador() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
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
                fontWeight = FontWeight.Bold
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
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                    placeholder = { Text("Escribe para filtrar") }
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
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
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
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}