package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(navController: NavController, vm: MatchingViewModel = viewModel()) {

    val bg = Color(0xFFF6F7FB)
    val primaryBlue = Color(0xFF0A63C2)

    val vacantes by vm.vacantes.collectAsState()
    val resultados by vm.resultados.collectAsState()
    val colaboradores by vm.colaboradores.collectAsState()
    val loading by vm.loading.collectAsState()
    val message by vm.message.collectAsState()

    // Dropdown state: guardamos solo el id para persistir con rememberSaveable
    var expanded by remember { mutableStateOf(false) }
    var selectedVacanteId by rememberSaveable { mutableStateOf<String?>(null) }

    // Derivar el objeto seleccionado a partir del id y la lista de vacantes
    val selectedVacante: VacanteResponse? = selectedVacanteId?.let { id -> vacantes.firstOrNull { it.getIdValue() == id } }

    // Cuando se carguen vacantes y no exista selección, seleccionar la primera por defecto
    LaunchedEffect(Unit) {
        vm.loadVacantes()
        vm.loadColaboradores()
    }

    LaunchedEffect(vacantes) {
        if (vacantes.isNotEmpty() && selectedVacanteId == null) {
            selectedVacanteId = vacantes.firstOrNull()?.getIdValue()
        }
    }

    var umbral by remember { mutableStateOf(60f) }
    var showDetailFor by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = bg
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ==========================
            // CARD DE CONFIGURACIÓN
            // ==========================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Column(Modifier.padding(16.dp)) {

                        Text(
                            "Configurar matching",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Seleccionar vacante *", color = Color.Gray)

                        // ==========================
                        // DROPDOWN CORRECTO
                        // ==========================
                        // Reemplazo por Spinner estilo Android usando ExposedDropdownMenuBox (Material3)
                        Column {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedVacante?.nombrePerfil ?: "Seleccione una vacante",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Vacante") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    vacantes.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item.nombrePerfil) },
                                            onClick = {
                                                selectedVacanteId = item.getIdValue()
                                                expanded = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Mostrar información completa de la vacante seleccionada
                        selectedVacante?.let { vac ->
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            Text("Área: ${vac.area}")
                            Text("Urgencia: ${vac.urgencia ?: "-"}")
                            Spacer(Modifier.height(8.dp))

                            Text("Skills requeridos (Críticas)", fontWeight = FontWeight.SemiBold)
                            val criticas = vac.skillsRequeridos.filter { it.esCritico }
                            if (criticas.isEmpty()) Text("- Ninguna") else {
                                criticas.forEach { s ->
                                    Text("• ${s.nombre} — Nivel: ${s.nivelDeseado}")
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text("Skills requeridos (No críticas)", fontWeight = FontWeight.SemiBold)
                            val noCrit = vac.skillsRequeridos.filter { !it.esCritico }
                            if (noCrit.isEmpty()) Text("- Ninguna") else {
                                noCrit.forEach { s ->
                                    Text("• ${s.nombre} — Nivel: ${s.nivelDeseado}")
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Text("Certificaciones requeridas:")
                            if (vac.certificacionesRequeridas.isEmpty()) Text("- Ninguna") else
                                vac.certificacionesRequeridas.forEach { c -> Text("• $c") }

                        }

                        Spacer(Modifier.height(12.dp))

                        // ==========================
                        // SLIDER UMBRAL 10-100
                        // ==========================
                        Text("Umbral mínimo (%)", color = Color.Gray)

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Slider(
                                value = umbral,
                                onValueChange = { umbral = it.coerceIn(10f, 100f) },
                                valueRange = 10f..100f,
                                steps = 9,
                                modifier = Modifier.weight(1f)
                            )

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
                                    Text("${umbral.toInt()}%")
                                }
                            }
                        }
                    }
                }
            }

            // ==========================
            // BOTÓN EJECUTAR MATCHING
            // ==========================
            item {
                Button(
                    onClick = {
                        selectedVacante?.let { vac ->
                            scope.launch {
                                vm.ejecutarMatching(
                                    vacante = vac,
                                    umbral = umbral.toInt(),
                                    guardarProceso = true
                                )
                            }
                        }
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

                if (loading) {
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator()
                }

                message?.let { msg ->
                    Spacer(Modifier.height(8.dp))
                    Text(msg, color = Color(0xFF8A2F2F))
                }
            }

            // ==========================
            // RESULTADOS
            // ==========================
            if (resultados.isEmpty()) {

                item {
                    Column(Modifier.fillMaxWidth()) {
                        Text("No hay candidatos para mostrar", color = Color.Gray)

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    vm.crearAlerta(
                                        tipo = "Brecha de skills",
                                        descripcion = "No se encontraron colaboradores que cumplan el umbral para la vacante ${selectedVacante?.nombrePerfil}",
                                        vacanteId = selectedVacante?.getIdValue()
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Crear alerta de brecha de skills")
                        }
                    }
                }

            } else {

                itemsIndexed(resultados) { _, result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDetailFor = result.colaboradorId },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${result.nombres} ${result.apellidos}", fontWeight = FontWeight.SemiBold)
                                Text("Match: ${"%.2f".format(result.puntaje)}%")
                            }
                            if (result.disponibleParaMovilidad) {
                                Text("Disponible", color = Color(0xFF0C8E32))
                            }
                        }
                    }
                }
            }

            // ==========================
            // BOTÓN ADICIONAL SI HAY VACANTE SELECCIONADA
            // ==========================
            item {
                if (selectedVacante != null) {
                    Button(
                        onClick = {
                            scope.launch {
                                vm.crearAlerta(
                                    tipo = "Colaboradores solicitados",
                                    descripcion = "El administrador solicitó colaboradores para la vacante ${selectedVacante.nombrePerfil}",
                                    vacanteId = selectedVacante.getIdValue()
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0E7AE6),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Crear alerta: Colaboradores solicitados", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ==========================
    // DETALLE DEL COLABORADOR
    // ==========================
    showDetailFor?.let { id ->
        val col = colaboradores.firstOrNull { it.id == id }

        if (col != null) {
            AlertDialog(
                onDismissRequest = { showDetailFor = null },
                confirmButton = {
                    TextButton(onClick = { showDetailFor = null }) {
                        Text("Cerrar")
                    }
                },
                title = { Text("${col.nombres} ${col.apellidos}") },
                text = {
                    Column {
                        Text("Área: ${col.area}")
                        Text("Rol: ${col.rolLaboral}")
                        Text("Correo: ${col.correo}")
                        Text("Estado: ${col.estado}")
                        Spacer(Modifier.height(8.dp))

                        Text("Skills técnicas:")
                        val tecnicas = col.skills.filter { it.tipo.equals("TECNICO", true) }
                        if (tecnicas.isEmpty()) Text("- Ninguna") else tecnicas.forEach { s ->
                            Text("- ${s.nombre} (Nivel ${s.nivel})")
                        }

                        Spacer(Modifier.height(6.dp))
                        Text("Skills blandas:")
                        val blandas = col.skills.filter { it.tipo.equals("BLANDO", true) }
                        if (blandas.isEmpty()) Text("- Ninguna") else blandas.forEach { s ->
                            Text("- ${s.nombre} (Nivel ${s.nivel})")
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Certificaciones:")
                        if (col.certificaciones.isEmpty()) Text("- Ninguna") else col.certificaciones.forEach { c ->
                            Text("- ${c.nombre} — Vencimiento: ${c.fechaVencimiento ?: "-"}")
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Disponible para movilidad: ${if (col.disponibleParaMovilidad) "Sí" else "No"}")

                        Spacer(Modifier.height(8.dp))
                        Text("Fecha registro: ${col.fechaRegistro ?: "-"}")
                    }
                }
            )
        }
    }
}
