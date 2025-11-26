package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.common.DatePickerTextField
import com.example.project_3_tcs_grupo4_dam.presentation.components.common.toIsoString
import java.time.LocalDate

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudCertificacionScreen(
    navController: NavController,
    viewModel: SolicitudColaboradorViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }
    var fechaObtencion by remember { mutableStateOf<LocalDate?>(null) }
    var fechaVencimiento by remember { mutableStateOf<LocalDate?>(null) }
    var pdfUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fechaObtencionError by remember { mutableStateOf(false) }
    var fechaVencimientoError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()

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
                        text = "Nueva Certificación",
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
                text = "Datos de la Certificación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la certificación *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = institucion,
                onValueChange = { institucion = it },
                label = { Text("Institución *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐ DatePicker para fecha de obtención (obligatorio)
            DatePickerTextField(
                label = "Fecha de obtención",
                selectedDate = fechaObtencion,
                onDateSelected = {
                    fechaObtencion = it
                    fechaObtencionError = false
                    // Validar que fecha de vencimiento sea >= fecha de obtención
                    if (fechaVencimiento != null && it != null && fechaVencimiento!! < it) {
                        fechaVencimientoError = true
                    } else {
                        fechaVencimientoError = false
                    }
                },
                isRequired = true,
                isError = fechaObtencionError,
                errorMessage = if (fechaObtencionError) "La fecha de obtención es obligatoria" else null,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐ DatePicker para fecha de vencimiento (opcional)
            DatePickerTextField(
                label = "Fecha de vencimiento",
                selectedDate = fechaVencimiento,
                onDateSelected = {
                    fechaVencimiento = it
                    fechaVencimientoError = false
                    // Validar que sea >= fecha de obtención
                    if (it != null && fechaObtencion != null && it < fechaObtencion!!) {
                        fechaVencimientoError = true
                    }
                },
                isRequired = false,
                isError = fechaVencimientoError,
                errorMessage = if (fechaVencimientoError) "Debe ser igual o posterior a la fecha de obtención" else null,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = pdfUrl,
                onValueChange = { pdfUrl = it },
                label = { Text("URL del PDF (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
                        fechaObtencionError = false
                        fechaVencimientoError = false

                        // Validaciones
                        var hasError = false

                        if (nombre.isBlank() || institucion.isBlank()) {
                            errorMessage = "Nombre e institución son obligatorios"
                            hasError = true
                        }

                        if (fechaObtencion == null) {
                            fechaObtencionError = true
                            errorMessage = "La fecha de obtención es obligatoria"
                            hasError = true
                        }

                        if (fechaVencimiento != null && fechaObtencion != null && fechaVencimiento!! < fechaObtencion!!) {
                            fechaVencimientoError = true
                            errorMessage = "La fecha de vencimiento debe ser igual o posterior a la de obtención"
                            hasError = true
                        }

                        if (!hasError) {
                            android.util.Log.d("SolicitudCertificacion", "Creando solicitud de certificación: $nombre")
                            android.util.Log.d("SolicitudCertificacion", "Fecha obtención: ${fechaObtencion?.toIsoString()}")
                            android.util.Log.d("SolicitudCertificacion", "Fecha vencimiento: ${fechaVencimiento?.toIsoString()}")

                            viewModel.crearSolicitudCertificacion(
                                nombre.trim(),
                                institucion.trim(),
                                fechaObtencion!!.toIsoString(), // Convertir a formato ISO
                                fechaVencimiento?.toIsoString(), // null o ISO
                                pdfUrl.takeIf { it.isNotBlank() }
                            )
                            android.util.Log.d("SolicitudCertificacion", "Navegando de vuelta")
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
        }
    }
}
