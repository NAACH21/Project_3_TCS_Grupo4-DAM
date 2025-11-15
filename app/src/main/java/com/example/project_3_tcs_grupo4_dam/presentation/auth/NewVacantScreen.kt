// NuevaVacanteScreen.kt
package com.tuapp.vacantes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.auth.BottomBarVacantes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewVacantScreen(
    navController: NavController,
    onBack: () -> Unit = {}
) {
    // Estados simples del formulario
    var nombrePerfil by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf("Media") }
    var estado by remember { mutableStateOf("Activa") }

    var skillNombre by remember { mutableStateOf("") }
    var skillTipo by remember { mutableStateOf("Técnico") }
    var skillNivel by remember { mutableStateOf("Intermedio") }

    var certNombre by remember { mutableStateOf("") }

    val listaAreas = listOf("Tecnología", "Business Intelligence", "Diseño", "Proyectos")
    val listaUrgencia = listOf("Alta", "Media", "Baja")
    val listaEstado = listOf("Activa", "En pausa", "Cerrada")
    val listaTipoSkill = listOf("Técnico", "Blanda")
    val listaNivelSkill = listOf("Básico", "Intermedio", "Avanzado")

    Scaffold(
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
            BottomBarVacantes()
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

            // Volver
            Row(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = Color(0xFF4B5563)
                )
                Text(
                    text = "Volver",
                    color = Color(0xFF4B5563),
                    fontSize = 14.sp
                )
            }

            // ------------ Datos de la vacante -------------
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Datos de la vacante",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    OutlinedTextField(
                        value = nombrePerfil,
                        onValueChange = { nombrePerfil = it },
                        label = { Text("Nombre del perfil *") },
                        placeholder = { Text("Ej: Desarrollador Full Stack Senior") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Área
                    ExposedDropdown(
                        label = "Área *",
                        value = area,
                        onValueChange = { area = it },
                        options = listaAreas,
                        placeholder = "Seleccione un área"
                    )

                    OutlinedTextField(
                        value = rol,
                        onValueChange = { rol = it },
                        label = { Text("Rol *") },
                        placeholder = { Text("Ej: Desarrollador") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Fecha de inicio") },
                        placeholder = { Text("dd/mm/aaaa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Urgencia
                    ExposedDropdown(
                        label = "Urgencia",
                        value = urgencia,
                        onValueChange = { urgencia = it },
                        options = listaUrgencia,
                        placeholder = "Seleccionar"
                    )

                    // Estado
                    ExposedDropdown(
                        label = "Estado",
                        value = estado,
                        onValueChange = { estado = it },
                        options = listaEstado,
                        placeholder = "Seleccionar"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ------------ Skills requeridos -------------
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skills requeridos",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Button(
                            onClick = { /* TODO agregar skill a la lista */ },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar skill",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = skillNombre,
                        onValueChange = { skillNombre = it },
                        label = { Text("Nombre del skill") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdown(
                            label = "",
                            value = skillTipo,
                            onValueChange = { skillTipo = it },
                            options = listaTipoSkill,
                            placeholder = "Tipo"
                        )
                        ExposedDropdown(
                            label = "",
                            value = skillNivel,
                            onValueChange = { skillNivel = it },
                            options = listaNivelSkill,
                            placeholder = "Nivel"
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ------------ Certificaciones -------------
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Certificaciones deseadas",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Button(
                            onClick = { /* TODO agregar certificación */ },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar certificación",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = certNombre,
                        onValueChange = { certNombre = it },
                        label = { Text("Nombre de la certificación") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón Guardar
            Button(
                onClick = {
                    // TODO: enviar datos al backend
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Guardar vacante")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * Componente reutilizable para los combos estilo "dropdown" de Material 3.
 * */
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

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = if (label.isNotEmpty()) { { Text(label) } } else null,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(10.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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

