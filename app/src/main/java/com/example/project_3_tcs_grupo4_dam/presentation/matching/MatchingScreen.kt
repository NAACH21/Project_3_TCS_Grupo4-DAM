package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// ✔ Para evitar warnings por ExposedDropdownMenu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(navController: NavController, vm: MatchingViewModel = viewModel()) {

    val bg = Color(0xFFF6F7FB)
    val primaryBlue = Color(0xFF0A63C2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matching Inteligente", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0E4F9C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        },
        containerColor = bg
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            //  CARD CONFIGURAR MATCHING
            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            "Configurar matching",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        // ----------------------
                        // Seleccionar vacante
                        // ----------------------
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                            Text(
                                "Seleccionar vacante *",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            var expanded by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = vm.selectedVacante,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Seleccione una vacante") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    vm.vacantesMock.forEach { v ->
                                        DropdownMenuItem(
                                            text = { Text(v) },
                                            onClick = {
                                                vm.selectedVacante = v
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // --------------------------
                        // Slider umbral de matching
                        // --------------------------
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                            Text(
                                "Umbral de match mínimo (%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Slider(
                                    value = vm.umbralMatch,
                                    onValueChange = { vm.umbralMatch = it },
                                    valueRange = 0f..100f,
                                    steps = 99,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(Modifier.width(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF0F2F6)
                                ) {
                                    Box(
                                        Modifier
                                            .width(56.dp)
                                            .height(36.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(vm.umbralMatch.toInt().toString())
                                    }
                                }
                            }

                            Text(
                                "Por defecto: 60%. Solo se mostrarán candidatos con match igual o superior al umbral.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            //  CARD SKILLS REQUERIDOS
            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Skills técnicos requeridos",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Button(
                                onClick = { vm.agregarSkill() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0A63C2),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("+ Agregar", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Text(
                            "El matching se realiza únicamente en base a skills técnicos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        // Lista de skills
                        if (vm.listaSkills.isEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF6F7FB), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Text("Añade los skills técnicos requeridos para la vacante.", color = Color.Gray)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                vm.listaSkills.forEachIndexed { index, skill ->
                                    SkillItem(
                                        nombre = skill.nombre,
                                        nivel = skill.nivel,
                                        niveles = vm.nivelesSkill,
                                        onNombreChange = { vm.actualizarSkillNombre(index, it) },
                                        onNivelChange = { vm.actualizarSkillNivel(index, it) },
                                        onRemove = { vm.eliminarSkill(index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            //   BOTÓN EJECUTAR MATCHING + MENSAJE
            // ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            item {
                Button(
                    onClick = {
                        val resultados = vm.ejecutarMatchingMock()   // ✔ EXISTE

                        println(resultados)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Ejecutar Matching", fontWeight = FontWeight.SemiBold)
                }

                vm.mensajeSistema?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillItem(
    nombre: String,
    nivel: String,
    niveles: List<String>,
    onNombreChange: (String) -> Unit,
    onNivelChange: (String) -> Unit,
    onRemove: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre del skill técnico") }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                var expanded by remember { mutableStateOf(false) }

                Column(Modifier.weight(1f)) {

                    Text("Nivel requerido", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {

                        TextField(
                            value = nivel,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )

                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            niveles.forEach { n ->
                                DropdownMenuItem(
                                    text = { Text(n) },
                                    onClick = {
                                        onNivelChange(n)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                ) {
                    Icon(Icons.Filled.Delete, "Eliminar", tint = Color(0xFFD32F2F))
                }
            }
        }
    }
}
