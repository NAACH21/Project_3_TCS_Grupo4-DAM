package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudActualizacionSkillsScreen(
    navController: NavController,
    viewModel: SolicitudColaboradorViewModel
) {
    var cambiosSkills by remember { mutableStateOf(listOf<CambioSkillItemUI>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showTipoSeleccion by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val misSkillsActuales by viewModel.misSkillsActuales.collectAsState()
    val skillsCatalogo by viewModel.skillsCatalogo.collectAsState()
    val nivelesSkill by viewModel.nivelesSkill.collectAsState()

    // Observar errores del ViewModel
    val vmErrorMessage by viewModel.errorMessage.collectAsState()
    LaunchedEffect(vmErrorMessage) {
        vmErrorMessage?.let {
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
                        text = "Actualizar Skills",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showTipoSeleccion = true },
                containerColor = TCSBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Cambio")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Cambios de Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Agrega los cambios que deseas solicitar en tus skills",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cambiosSkills.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay cambios agregados",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Usa el botón + para agregar un cambio de skill",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF999999)
                        )
                    }
                }
            } else {
                cambiosSkills.forEachIndexed { index, item ->
                    CambioSkillCard(
                        item = item,
                        misSkillsActuales = misSkillsActuales,
                        skillsCatalogo = skillsCatalogo,
                        nivelesSkill = nivelesSkill,
                        onUpdate = { updated ->
                            cambiosSkills = cambiosSkills.toMutableList().apply {
                                set(index, updated)
                            }
                        },
                        onRemove = {
                            cambiosSkills = cambiosSkills.filterIndexed { i, _ -> i != index }
                        }
                    )

                    if (index < cambiosSkills.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = errorMessage!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        errorMessage = null
                        if (cambiosSkills.isEmpty()) {
                            errorMessage = "Agrega al menos un cambio de skill"
                        } else if (cambiosSkills.any {
                            (it.skillExistenteSeleccionada == null && it.skillNuevaSeleccionada == null) ||
                            it.nivelPropuesto == null
                        }) {
                            errorMessage = "Completa todos los campos requeridos"
                        } else {
                            android.util.Log.d("SolicitudSkills", "Creando solicitud de skills con ${cambiosSkills.size} cambios")
                            val modelos = cambiosSkills.map { it.toUiModel() }
                            viewModel.crearSolicitudActualizacionSkills(modelos)
                            android.util.Log.d("SolicitudSkills", "Navegando de vuelta")
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TCSBlue),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar Solicitud")
                    }
                }
            }

            // Espacio para el FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Bottom sheet para seleccionar tipo de cambio
    if (showTipoSeleccion) {
        ModalBottomSheet(
            onDismissRequest = { showTipoSeleccion = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "¿Qué deseas hacer?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    onClick = {
                        cambiosSkills = cambiosSkills + CambioSkillItemUI(esNueva = false)
                        showTipoSeleccion = false
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = TCSBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Actualizar skill existente",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Modificar nivel o criticidad de tus skills actuales",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    onClick = {
                        cambiosSkills = cambiosSkills + CambioSkillItemUI(esNueva = true)
                        showTipoSeleccion = false
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TCSBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Agregar nueva skill",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Solicitar agregar una skill que aún no tienes",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CambioSkillCard(
    item: CambioSkillItemUI,
    misSkillsActuales: List<SkillReadDto>,
    skillsCatalogo: List<CatalogoDtos.SkillCatalogItemDto>,
    nivelesSkill: List<CatalogoDtos.NivelSkillDto>,
    onUpdate: (CambioSkillItemUI) -> Unit,
    onRemove: () -> Unit
) {
    var expandedSkill by remember { mutableStateOf(false) }
    var expandedNivel by remember { mutableStateOf(false) }

    val skillsDisponibles = skillsCatalogo.filter { catalogoSkill ->
        misSkillsActuales.none { it.nombre.equals(catalogoSkill.nombre, ignoreCase = true) }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.esNueva) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.esNueva) "Agregar Nueva Skill" else "Actualizar Skill Existente",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (item.esNueva) Color(0xFF2E7D32) else Color(0xFF1976D2)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de skill
            ExposedDropdownMenuBox(
                expanded = expandedSkill,
                onExpandedChange = { expandedSkill = it }
            ) {
                OutlinedTextField(
                    value = if (item.esNueva) {
                        item.skillNuevaSeleccionada?.nombre ?: ""
                    } else {
                        item.skillExistenteSeleccionada?.nombre ?: ""
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (item.esNueva) "Skill del catálogo *" else "Tu skill *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSkill) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedSkill,
                    onDismissRequest = { expandedSkill = false }
                ) {
                    if (item.esNueva) {
                        if (skillsDisponibles.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Ya tienes todas las skills del catálogo") },
                                onClick = { expandedSkill = false },
                                enabled = false
                            )
                        } else {
                            skillsDisponibles.forEach { skill ->
                                DropdownMenuItem(
                                    text = { Text("${skill.nombre} (${skill.tipo})") },
                                    onClick = {
                                        onUpdate(item.copy(skillNuevaSeleccionada = skill))
                                        expandedSkill = false
                                    }
                                )
                            }
                        }
                    } else {
                        misSkillsActuales.forEach { skill ->
                            DropdownMenuItem(
                                text = { Text("${skill.nombre} - Nivel ${skill.nivel}") },
                                onClick = {
                                    onUpdate(item.copy(skillExistenteSeleccionada = skill))
                                    expandedSkill = false
                                }
                            )
                        }
                    }
                }
            }

            // Mostrar info actual si es actualización
            if (!item.esNueva && item.skillExistenteSeleccionada != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Valores Actuales",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text("Tipo: ${item.skillExistenteSeleccionada.tipo}", style = MaterialTheme.typography.bodySmall)
                        Text("Nivel actual: ${item.skillExistenteSeleccionada.nivel}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else if (item.esNueva && item.skillNuevaSeleccionada != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Tipo: ${item.skillNuevaSeleccionada.tipo}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nivel propuesto
            ExposedDropdownMenuBox(
                expanded = expandedNivel,
                onExpandedChange = { expandedNivel = it }
            ) {
                OutlinedTextField(
                    value = item.nivelPropuesto?.let { "${it.codigo} - ${it.descripcion}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel propuesto *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNivel) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedNivel,
                    onDismissRequest = { expandedNivel = false }
                ) {
                    nivelesSkill.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text("${nivel.codigo} - ${nivel.descripcion}") },
                            onClick = {
                                onUpdate(item.copy(nivelPropuesto = nivel))
                                expandedNivel = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Switch crítico
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Marcar como crítico?", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = item.esCriticoPropuesto,
                    onCheckedChange = { onUpdate(item.copy(esCriticoPropuesto = it)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Motivo
            OutlinedTextField(
                value = item.motivo,
                onValueChange = { onUpdate(item.copy(motivo = it)) },
                label = { Text("Motivo (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }
    }
}

// Modelo UI interno para la pantalla
private data class CambioSkillItemUI(
    val skillExistenteSeleccionada: SkillReadDto? = null,
    val skillNuevaSeleccionada: CatalogoDtos.SkillCatalogItemDto? = null,
    val nivelPropuesto: CatalogoDtos.NivelSkillDto? = null,
    val esCriticoPropuesto: Boolean = false,
    val motivo: String = "",
    val esNueva: Boolean = false
) {
    fun toUiModel(): CambioSkillUiModel {
        return if (esNueva) {
            CambioSkillUiModel(
                nombre = skillNuevaSeleccionada!!.nombre,
                tipo = skillNuevaSeleccionada.tipo,
                nivelActual = null,
                nivelPropuesto = nivelPropuesto!!.codigo,
                esCriticoActual = null,
                esCriticoPropuesto = esCriticoPropuesto,
                motivo = motivo,
                esNueva = true
            )
        } else {
            CambioSkillUiModel(
                nombre = skillExistenteSeleccionada!!.nombre,
                tipo = skillExistenteSeleccionada.tipo,
                nivelActual = skillExistenteSeleccionada.nivel,
                nivelPropuesto = nivelPropuesto!!.codigo,
                esCriticoActual = skillExistenteSeleccionada.esCritico,
                esCriticoPropuesto = esCriticoPropuesto,
                motivo = motivo,
                esNueva = false
            )
        }
    }
}
