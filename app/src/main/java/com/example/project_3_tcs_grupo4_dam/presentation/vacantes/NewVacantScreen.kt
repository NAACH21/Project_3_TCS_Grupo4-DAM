// NuevaVacanteScreen.kt
package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.SkillRequerido
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewVacantScreen(
    navController: NavController,
    onBack: () -> Unit = {},
    newVacantViewModel: NewVacantViewModel = viewModel()
) {
    // Estados del formulario
    var nombrePerfil by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf("Media") }
    
    // FIX: Estado por defecto "ABIERTA" (Mayúsculas)
    var estado by remember { mutableStateOf("ABIERTA") }

    // Estados para añadir skills
    var skillNombre by remember { mutableStateOf("") }
    var skillTipo by remember { mutableStateOf("Técnico") }
    var skillNivel by remember { mutableStateOf("Intermedio") }
    var skillEsCritico by remember { mutableStateOf(false) }
    val skillsRequeridos = remember { mutableStateListOf<SkillRequerido>() }

    // Estados para añadir certificaciones
    var certNombre by remember { mutableStateOf("") }
    val certificacionesRequeridas = remember { mutableStateListOf<String>() }

    val saveStatus by newVacantViewModel.saveStatus.collectAsState()
    val anuncioStatus by newVacantViewModel.anuncioStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // NUEVO: Estado para mostrar diálogo de anuncio
    var showAnuncioDialog by remember { mutableStateOf(false) }
    var vacanteIdCreada by remember { mutableStateOf("") }

    // NUEVO: Manejar estado de guardado con captura de ID
    LaunchedEffect(saveStatus) {
        when (val status = saveStatus) {
            is SaveResult.Success -> {
                // Capturar el ID de la vacante creada
                // FIX: Accedemos al ID a través del objeto vacante
                vacanteIdCreada = status.vacante.id

                // Mostrar diálogo preguntando si desea notificar
                showAnuncioDialog = true

                newVacantViewModel.resetSaveStatus()
            }
            is SaveResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = status.message,
                        duration = SnackbarDuration.Long
                    )
                }
                newVacantViewModel.resetSaveStatus()
            }
            else -> {}
        }
    }

    // NUEVO: Manejar estado de anuncio
    LaunchedEffect(anuncioStatus) {
        when (val status = anuncioStatus) {
            is AnuncioResult.Success -> {
                // FIX: Usar Toast en lugar de Snackbar para que el mensaje persista
                // al cerrar la pantalla (popBackStack)
                Toast.makeText(
                    context, 
                    "¡Notificación enviada correctamente a los colaboradores!", 
                    Toast.LENGTH_LONG
                ).show()
                
                // Cerrar diálogo y volver a la lista
                showAnuncioDialog = false
                navController.popBackStack()
                newVacantViewModel.resetAnuncioStatus()
            }
            is AnuncioResult.Error -> {
                // En caso de error, sí usamos Snackbar porque nos quedamos en la pantalla
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = status.message,
                        duration = SnackbarDuration.Long
                    )
                }
                newVacantViewModel.resetAnuncioStatus()
            }
            else -> {}
        }
    }


    val listaAreas = listOf("Tecnología", "Business Intelligence", "Diseño", "Proyectos")
    val listaUrgencia = listOf("Alta", "Media", "Baja")
    
    // FIX: "ABIERTA" (Mayúsculas) en la lista de opciones
    val listaEstado = listOf("ABIERTA", "Activa", "En pausa", "Cerrada")
    
    val listaTipoSkill = listOf("Técnico", "Blanda")
    val listaNivelSkill = listOf("Básico", "Intermedio", "Avanzado")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nueva Vacante", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1959B8)
                ),
                actions = {
                    IconButton(onClick = { /* TODO notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFF3F4F6))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver"
                )
                Text("Volver")
            }

            // --- Formulario ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos de la vacante", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = nombrePerfil, onValueChange = { nombrePerfil = it }, label = { Text("Nombre del perfil *") }, modifier = Modifier.fillMaxWidth())
                    ExposedDropdown("Área *", area, { area = it }, listaAreas, "Seleccione un área")
                    OutlinedTextField(value = rol, onValueChange = { rol = it }, label = { Text("Rol *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fechaInicio, onValueChange = { fechaInicio = it }, label = { Text("Fecha de inicio") }, placeholder = { Text("dd/mm/aaaa") }, modifier = Modifier.fillMaxWidth())
                    ExposedDropdown("Urgencia", urgencia, { urgencia = it }, listaUrgencia, "Seleccionar")
                    ExposedDropdown("Estado", estado, { estado = it }, listaEstado, "Seleccionar")
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Skills ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Skills requeridos", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Button(
                            onClick = {
                                if (skillNombre.isNotBlank()) {
                                    val nivelInt = when (skillNivel) {
                                        "Básico" -> 1
                                        "Intermedio" -> 2
                                        "Avanzado" -> 3
                                        else -> 0
                                    }
                                    skillsRequeridos.add(SkillRequerido(skillNombre, skillTipo, nivelInt, skillEsCritico))
                                    // Limpiar campos
                                    skillNombre = ""
                                    skillTipo = "Técnico"
                                    skillNivel = "Intermedio"
                                    skillEsCritico = false
                                }
                            },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar skill", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", fontSize = 13.sp)
                        }
                    }
                    OutlinedTextField(value = skillNombre, onValueChange = { skillNombre = it }, label = { Text("Nombre del skill") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdown("Tipo", skillTipo, { skillTipo = it }, listaTipoSkill, "Tipo", Modifier.weight(1f))
                        ExposedDropdown("Nivel", skillNivel, { skillNivel = it }, listaNivelSkill, "Nivel", Modifier.weight(1f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = skillEsCritico, onCheckedChange = { skillEsCritico = it })
                        Text("Es crítico")
                    }

                    // Lista de skills agregados
                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        skillsRequeridos.forEach { skill ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text("${skill.nombre} (${skill.tipo}) - Nivel: ${skill.nivelDeseado}") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Eliminar skill",
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { skillsRequeridos.remove(skill) }
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            // --- Certificaciones ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Certificaciones deseadas", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Button(
                            onClick = {
                                if (certNombre.isNotBlank()) {
                                    certificacionesRequeridas.add(certNombre)
                                    certNombre = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, "Agregar", Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", fontSize = 13.sp)
                        }
                    }
                    OutlinedTextField(value = certNombre, onValueChange = { certNombre = it }, label = { Text("Nombre de la certificación") }, modifier = Modifier.fillMaxWidth())

                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        certificacionesRequeridas.forEach { cert ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(cert) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, "Eliminar", Modifier.size(18.dp).clickable { certificacionesRequeridas.remove(cert) })
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    newVacantViewModel.saveVacante(
                        nombrePerfil = nombrePerfil,
                        area = area,
                        rolLaboral = rol,
                        skillsRequeridos = skillsRequeridos,
                        certificacionesRequeridas = certificacionesRequeridas,
                        fechaInicio = fechaInicio,
                        urgencia = urgencia,
                        estadoVacante = estado
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveStatus != SaveResult.Loading
            ) {
                if (saveStatus == SaveResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Guardar vacante")
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // NUEVO: Diálogo de confirmación de anuncio
        if (showAnuncioDialog) {
            AnuncioVacanteDialog(
                onConfirm = {
                    // FIX: Validación de estado "ABIERTA" (Mayúsculas)
                    if (estado == "ABIERTA") {
                        newVacantViewModel.notificarAhora(vacanteIdCreada)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Para notificar, la vacante debe estar en estado 'ABIERTA'.",
                                duration = SnackbarDuration.Long
                            )
                        }
                        showAnuncioDialog = false
                    }
                },
                onDismiss = {
                    // Solo cerrar y volver sin enviar notificación
                    showAnuncioDialog = false
                    navController.popBackStack()
                },
                isLoading = anuncioStatus == AnuncioResult.Loading
            )
        }
    }
}

/**
 * NUEVO: Diálogo para preguntar si desea notificar la vacante
 */
@Composable
fun AnuncioVacanteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF1959B8)
                )
                Text(
                    text = "¿Notificar Vacante?",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = "La vacante ha sido creada exitosamente. ¿Deseas enviar una notificación por correo a los colaboradores elegibles ahora?",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1959B8)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sí, notificar ahora")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("No, notificar después", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
