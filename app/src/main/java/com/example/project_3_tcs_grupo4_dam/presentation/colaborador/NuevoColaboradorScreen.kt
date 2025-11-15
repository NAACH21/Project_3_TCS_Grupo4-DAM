package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.layout.*
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
import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoColaboradorScreen(navController: NavController) {
    val viewModel: NuevoColaboradorViewModel = viewModel()

    // Observar estados
    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val area by viewModel.area.collectAsState()
    val rolActual by viewModel.rolActual.collectAsState()

    val allSkills by viewModel.allSkills.collectAsState()
    val skillSearchText by viewModel.skillSearchText.collectAsState()
    val selectedSkills by viewModel.selectedSkills.collectAsState()

    val niveles by viewModel.niveles.collectAsState()
    val selectedNivel by viewModel.selectedNivel.collectAsState()

    val certificaciones by viewModel.certificaciones.collectAsState()

    val disponibilidadEstado by viewModel.disponibilidadEstado.collectAsState()
    val disponibilidadDias by viewModel.disponibilidadDias.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Colaborador") },
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
                DatosPersonalesSection(
                    nombres = nombres,
                    apellidos = apellidos,
                    area = area,
                    rolActual = rolActual,
                    onNombresChange = viewModel::onNombresChange,
                    onApellidosChange = viewModel::onApellidosChange,
                    onAreaChange = viewModel::onAreaChange,
                    onRolActualChange = viewModel::onRolActualChange
                )

                // Sección 2: Skills
                SkillsSection(
                    allSkills = allSkills,
                    selectedSkills = selectedSkills,
                    searchText = skillSearchText,
                    onSearchChange = viewModel::onSkillSearchChange,
                    onToggleSkill = viewModel::toggleSkillSelection,
                    onRemoveSkill = viewModel::removeSkill
                )

                // Sección 3: Nivel
                NivelSection(
                    niveles = niveles,
                    selectedNivel = selectedNivel,
                    onNivelSelected = viewModel::onNivelSelected
                )

                // Sección 4: Certificaciones
                CertificacionesSection(
                    certificaciones = certificaciones,
                    onAddCertificacion = viewModel::addCertificacion,
                    onUpdateNombre = viewModel::updateCertificacionNombre,
                    onUpdateUrl = viewModel::updateCertificacionUrl,
                    onUpdateFecha = viewModel::updateCertificacionFecha,
                    onRemove = viewModel::removeCertificacion
                )

                // Sección 5: Disponibilidad
                DisponibilidadSection(
                    estado = disponibilidadEstado,
                    dias = disponibilidadDias,
                    onEstadoChange = viewModel::onDisponibilidadEstadoChange,
                    onDiasChange = viewModel::onDisponibilidadDiasChange
                )

                // Botón Guardar
                Button(
                    onClick = { viewModel.saveColaborador() },
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
                    Text(if (isSaving) "Guardando..." else "Guardar colaborador")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DatosPersonalesSection(
    nombres: String,
    apellidos: String,
    area: String,
    rolActual: String,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onRolActualChange: (String) -> Unit
) {
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
                onValueChange = onNombresChange,
                label = { Text("Nombres *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = onApellidosChange,
                label = { Text("Apellidos *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = area,
                onValueChange = onAreaChange,
                label = { Text("Área *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = rolActual,
                onValueChange = onRolActualChange,
                label = { Text("Rol Actual *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun SkillsSection(
    allSkills: List<SkillDto>,
    selectedSkills: List<SkillDto>,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onToggleSkill: (SkillDto) -> Unit,
    onRemoveSkill: (SkillDto) -> Unit
) {
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
                text = "2. Skills *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                label = { Text("Buscar skill...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Mostrar skills filtrados para seleccionar
            if (searchText.isNotBlank()) {
                val filteredSkills = allSkills.filter {
                    it.nombre.contains(searchText, ignoreCase = true)
                }.take(5)

                if (filteredSkills.isNotEmpty()) {
                    Text(
                        text = "Resultados:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    filteredSkills.forEach { skill ->
                        val isSelected = selectedSkills.any { it.id == skill.id }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleSkill(skill) },
                            label = { Text("${skill.nombre} (${skill.tipo})") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Skills seleccionados
            if (selectedSkills.isNotEmpty()) {
                Text(
                    text = "Skills seleccionados:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                selectedSkills.forEach { skill ->
                    AssistChip(
                        onClick = { onRemoveSkill(skill) },
                        label = { Text(skill.nombre) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NivelSection(
    niveles: List<NivelSkillDto>,
    selectedNivel: NivelSkillDto?,
    onNivelSelected: (NivelSkillDto) -> Unit
) {
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
                text = "3. Nivel *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedNivel?.nombre ?: "Seleccionar nivel",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel de skill") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    niveles.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text("${nivel.nombre} (Código: ${nivel.codigo})") },
                            onClick = {
                                onNivelSelected(nivel)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificacionesSection(
    certificaciones: List<CertificacionCreateDto>,
    onAddCertificacion: () -> Unit,
    onUpdateNombre: (Int, String) -> Unit,
    onUpdateUrl: (Int, String) -> Unit,
    onUpdateFecha: (Int, String) -> Unit,
    onRemove: (Int) -> Unit
) {
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
                text = "4. Certificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = onAddCertificacion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Agregar certificación")
            }

            certificaciones.forEachIndexed { index, cert ->
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Certificación ${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(onClick = { onRemove(index) }) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar")
                            }
                        }

                        OutlinedTextField(
                            value = cert.nombre,
                            onValueChange = { onUpdateNombre(index, it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cert.imagenUrl ?: "",
                            onValueChange = { onUpdateUrl(index, it) },
                            label = { Text("URL de imagen") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cert.fechaObtencion ?: "",
                            onValueChange = { onUpdateFecha(index, it) },
                            label = { Text("Fecha (YYYY-MM-DDTHH:mm:ssZ)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisponibilidadSection(
    estado: String,
    dias: Int?,
    onEstadoChange: (String) -> Unit,
    onDiasChange: (String) -> Unit
) {
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
                text = "5. Disponibilidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            var expanded by remember { mutableStateOf(false) }
            val estados = listOf("Disponible", "Ocupado", "Inactivo")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { est ->
                        DropdownMenuItem(
                            text = { Text(est) },
                            onClick = {
                                onEstadoChange(est)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = dias?.toString() ?: "",
                onValueChange = onDiasChange,
                label = { Text("Días") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
