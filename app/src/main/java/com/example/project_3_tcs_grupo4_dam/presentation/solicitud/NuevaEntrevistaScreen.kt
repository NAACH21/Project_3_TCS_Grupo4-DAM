package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.presentation.components.common.DatePickerTextField
import com.example.project_3_tcs_grupo4_dam.presentation.components.common.toIsoString
import java.time.LocalDate

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaEntrevistaScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

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

    var colaboradores by remember { mutableStateOf<List<ColaboradorReadDto>>(emptyList()) }
    var isLoadingColaboradores by remember { mutableStateOf(true) }
    var colaboradorSeleccionado by remember { mutableStateOf<ColaboradorReadDto?>(null) }
    var showColaboradorSelector by remember { mutableStateOf(false) }

    var motivo by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("") }
    var fechaSugerida by remember { mutableStateOf<LocalDate?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val vmErrorMessage by viewModel.errorMessage.collectAsState()

    // Cargar lista de colaboradores
    LaunchedEffect(Unit) {
        try {
            val repo = ColaboradorRepositoryImpl()
            colaboradores = repo.getAllColaboradores()
            isLoadingColaboradores = false
            android.util.Log.d("NuevaEntrevista", "Colaboradores cargados: ${colaboradores.size}")
        } catch (e: Exception) {
            android.util.Log.e("NuevaEntrevista", "Error al cargar colaboradores", e)
            isLoadingColaboradores = false
        }
    }

    // Observar errores del ViewModel
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
                        text = "Nueva Entrevista de Desempeño",
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Datos de la Entrevista",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de colaborador
            OutlinedTextField(
                value = colaboradorSeleccionado?.let { "${it.nombres} ${it.apellidos} - ${it.area ?: "Sin área"}" } ?: "",
                onValueChange = { },
                label = { Text("Colaborador *") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = !isLoading && !isLoadingColaboradores,
                trailingIcon = {
                    IconButton(
                        onClick = { showColaboradorSelector = true },
                        enabled = !isLoading && !isLoadingColaboradores
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Seleccionar colaborador"
                        )
                    }
                },
                placeholder = { Text("Selecciona un colaborador") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Motivo
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = !isLoading,
                placeholder = { Text("Ej: Evaluación de desempeño anual") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Periodo
            OutlinedTextField(
                value = periodo,
                onValueChange = { periodo = it },
                label = { Text("Periodo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                placeholder = { Text("Ej: 2025, Q1 2025, etc.") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha sugerida usando DatePickerTextField
            DatePickerTextField(
                label = "Fecha sugerida",
                selectedDate = fechaSugerida,
                onDateSelected = { fechaSugerida = it },
                isRequired = false,
                enabled = !isLoading
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
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

                        // Validaciones
                        when {
                            colaboradorSeleccionado == null -> {
                                errorMessage = "Debe seleccionar un colaborador"
                            }
                            motivo.isBlank() -> {
                                errorMessage = "El motivo es obligatorio"
                            }
                            periodo.isBlank() -> {
                                errorMessage = "El periodo es obligatorio"
                            }
                            else -> {
                                android.util.Log.d("NuevaEntrevista", "Creando entrevista para: ${colaboradorSeleccionado!!.nombres}")
                                viewModel.crearSolicitudEntrevista(
                                    colaboradorId = colaboradorSeleccionado!!.id,
                                    motivo = motivo.trim(),
                                    periodo = periodo.trim(),
                                    fechaSugerida = fechaSugerida?.toIsoString()
                                )
                                navController.popBackStack()
                            }
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
                        Text("Crear Entrevista")
                    }
                }
            }
        }
    }

    // Diálogo selector de colaborador
    if (showColaboradorSelector) {
        AlertDialog(
            onDismissRequest = { showColaboradorSelector = false },
            title = { Text("Seleccionar Colaborador", fontWeight = FontWeight.Bold) },
            text = {
                if (isLoadingColaboradores) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TCSBlue)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        colaboradores.forEach { colab ->
                            TextButton(
                                onClick = {
                                    colaboradorSeleccionado = colab
                                    showColaboradorSelector = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = androidx.compose.ui.Alignment.Start
                                ) {
                                    Text(
                                        text = "${colab.nombres} ${colab.apellidos}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF333333)
                                    )
                                    Text(
                                        text = "${colab.area} - ${colab.rolLaboral}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColaboradorSelector = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}