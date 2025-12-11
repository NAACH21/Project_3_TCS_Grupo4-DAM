package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador.ColaboradorCard
import com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador.ColaboradoresHeader
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 🔹 Color Corporativo TCS
private val PrimaryBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradoresScreen(navController: NavController) {
    // ViewModel de esta pantalla
    val viewModel: ColaboradoresViewModel = viewModel()

    val colaboradores by viewModel.colaboradores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    // Estado para el diálogo de confirmación de eliminación
    var showDeleteDialog by remember { mutableStateOf(false) }
    var colaboradorToDelete by remember { mutableStateOf<ColaboradorReadDto?>(null) }

    // Estado para el diálogo de filtros
    var showFiltersDialog by remember { mutableStateOf(false) }

    // Refrescar la lista cuando la pantalla se vuelve visible
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && colaboradorToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar colaborador") },
            text = { Text("¿Está seguro que desea eliminar a ${colaboradorToDelete!!.nombres} ${colaboradorToDelete!!.apellidos}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarColaborador(colaboradorToDelete!!.id)
                        showDeleteDialog = false
                        colaboradorToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de filtros
    if (showFiltersDialog) {
        FiltrosDialog(
            viewModel = viewModel,
            onDismiss = { showFiltersDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Colaboradores",
                        color = Color.White
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔹 Header con búsqueda / filtros / carga masiva / nuevo colaborador
            ColaboradoresHeader(
                searchText = searchText,
                onSearchTextChange = { viewModel.onSearchTextChange(it) },
                onNuevoColaborador = { navController.navigate(Routes.COLABORADOR_FORM) },
                onCargaMasiva = { /* TODO: Abrir flujo de carga masiva */ },
                onFiltros = { showFiltersDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Contenido principal (lista, loading, error)
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    colaboradores.isEmpty() -> {
                        Text("Sin colaboradores por mostrar")
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(colaboradores) { colaborador ->
                                ColaboradorCard(
                                    colaborador = colaborador,
                                    onVerDetalle = {
                                        navController.navigate("${Routes.COLABORADOR_DETALLE}/${colaborador.id}")
                                    },
                                    onEditar = {
                                        navController.navigate("${Routes.COLABORADOR_FORM}/${colaborador.id}")
                                    },
                                    onEliminar = {
                                        colaboradorToDelete = colaborador
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Diálogo de filtros para colaboradores
 * Permite filtrar por área, tipo de skill, skill específica y rango de fechas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltrosDialog(
    viewModel: ColaboradoresViewModel,
    onDismiss: () -> Unit
) {
    val selectedArea by viewModel.selectedArea.collectAsState()
    val selectedTipoSkill by viewModel.selectedTipoSkill.collectAsState()
    val selectedSkill by viewModel.selectedSkill.collectAsState()
    val fechaInicio by viewModel.fechaInicio.collectAsState()
    val fechaFin by viewModel.fechaFin.collectAsState()

    // Listas disponibles
    val areasDisponibles = remember { viewModel.getAreasDisponibles() }
    val skillsDisponibles = remember { viewModel.getSkillsDisponibles() }
    val tiposSkill = listOf("Técnica", "Blanda")

    // Estados locales para dropdowns
    var expandedArea by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedSkill by remember { mutableStateOf(false) }

    // Estados para DatePickers
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filtro por Área
                Text(
                    text = "Área",
                    style = MaterialTheme.typography.labelLarge
                )
                ExposedDropdownMenuBox(
                    expanded = expandedArea,
                    onExpandedChange = { expandedArea = it }
                ) {
                    OutlinedTextField(
                        value = selectedArea ?: "Todas las áreas",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedArea,
                        onDismissRequest = { expandedArea = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las áreas") },
                            onClick = {
                                viewModel.onAreaSelected(null)
                                expandedArea = false
                            }
                        )
                        areasDisponibles.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area) },
                                onClick = {
                                    viewModel.onAreaSelected(area)
                                    expandedArea = false
                                }
                            )
                        }
                    }
                }

                // Filtro por Tipo de Skill
                Text(
                    text = "Tipo de Skill",
                    style = MaterialTheme.typography.labelLarge
                )
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = it }
                ) {
                    OutlinedTextField(
                        value = selectedTipoSkill ?: "Todos los tipos",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los tipos") },
                            onClick = {
                                viewModel.onTipoSkillSelected(null)
                                expandedTipo = false
                            }
                        )
                        tiposSkill.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    viewModel.onTipoSkillSelected(tipo)
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }

                // Filtro por Skill específica
                Text(
                    text = "Skill",
                    style = MaterialTheme.typography.labelLarge
                )
                ExposedDropdownMenuBox(
                    expanded = expandedSkill,
                    onExpandedChange = { expandedSkill = it }
                ) {
                    OutlinedTextField(
                        value = selectedSkill ?: "Todas las skills",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSkill) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSkill,
                        onDismissRequest = { expandedSkill = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las skills") },
                            onClick = {
                                viewModel.onSkillSelected(null)
                                expandedSkill = false
                            }
                        )
                        skillsDisponibles.forEach { skill ->
                            DropdownMenuItem(
                                text = { Text(skill) },
                                onClick = {
                                    viewModel.onSkillSelected(skill)
                                    expandedSkill = false
                                }
                            )
                        }
                    }
                }

                // Filtro por Fecha de Creación (Rango)
                Text(
                    text = "Periodo de Creación",
                    style = MaterialTheme.typography.labelLarge
                )

                // Fecha Inicio
                OutlinedTextField(
                    value = fechaInicio ?: "",
                    onValueChange = {},
                    label = { Text("Fecha Inicio") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerInicio = true }) {
                            Icon(Icons.Default.DateRange, "Seleccionar fecha", tint = PrimaryBlue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("yyyy-MM-dd") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        cursorColor = PrimaryBlue
                    )
                )

                // Fecha Fin
                OutlinedTextField(
                    value = fechaFin ?: "",
                    onValueChange = {},
                    label = { Text("Fecha Fin") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerFin = true }) {
                            Icon(Icons.Default.DateRange, "Seleccionar fecha", tint = PrimaryBlue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("yyyy-MM-dd") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        cursorColor = PrimaryBlue
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.limpiarFiltros()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Limpiar filtros")
            }
        }
    )

    // DatePicker para Fecha Inicio
    if (showDatePickerInicio) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString = formatter.format(Date(millis))
                            viewModel.onFechaInicioSelected(dateString)
                        }
                        showDatePickerInicio = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerInicio = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha Inicio",
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

    // DatePicker para Fecha Fin
    if (showDatePickerFin) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerFin = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString = formatter.format(Date(millis))
                            viewModel.onFechaFinSelected(dateString)
                        }
                        showDatePickerFin = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerFin = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha Fin",
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
