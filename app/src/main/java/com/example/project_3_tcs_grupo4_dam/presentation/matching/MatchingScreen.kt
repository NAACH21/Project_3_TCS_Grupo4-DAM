package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto

import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import kotlinx.coroutines.launch
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.platform.LocalContext
import com.example.project_3_tcs_grupo4_dam.utils.OpenPdfHelper

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
    val pdfGenerado by vm.pdfGenerado.collectAsState()

    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    var selectedVacanteId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedVacante: VacanteResponse? = selectedVacanteId?.let { id ->
        vacantes.firstOrNull { it.getIdValue() == id }
    }

    LaunchedEffect(Unit) {
        vm.loadVacantes()
        vm.loadColaboradores()
    }

    LaunchedEffect(vacantes) {
        if (vacantes.isNotEmpty() && selectedVacanteId == null) {
            selectedVacanteId = vacantes.first().getIdValue()
        }
    }

    var umbral by remember { mutableStateOf(60f) }
    var showDetailFor by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    pdfGenerado?.let { file ->
        LaunchedEffect(file) {
            snackbarHostState.showSnackbar("PDF generado correctamente")
            // Intentar abrir el PDF usando FileProvider y el helper
            val opened = OpenPdfHelper.abrirPdf(context, file)
            if (!opened) {
                // Si no hay app que pueda abrir el PDF, informar al usuario y dar la ruta
                snackbarHostState.showSnackbar("PDF guardado en: ${file.absolutePath} — no se encontró visor PDF instalado")
            }
        }
    }

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
                    containerColor = primaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bg
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ======================================================
            // BLOQUE PROFESIONAL: CONFIGURACIÓN
            // ======================================================
            item {
                InfoBlock(title = "Configurar Matching") {

                    Text("Seleccionar vacante *", color = Color.Gray)

                    Spacer(Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedVacante?.nombrePerfil ?: "Seleccione una vacante",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Vacante") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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

                    Spacer(Modifier.height(16.dp))

                    selectedVacante?.let { vac ->
                        InfoSectionVacante(vac)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Umbral mínimo (%)", color = Color.Gray)

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = umbral,
                            onValueChange = { umbral = it },
                            valueRange = 10f..100f,
                            steps = 9,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF0F2F6)
                        ) {
                            Box(
                                modifier = Modifier
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

            // ======================================================
            // BOTÓN EJECUTAR MATCHING
            // ======================================================
            item {
                Button(
                    onClick = {
                        selectedVacante?.let { vac ->
                            scope.launch {
                                vm.ejecutarMatching(vac, umbral.toInt(), true)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(primaryBlue)
                ) {
                    Text("Ejecutar Matching", fontWeight = FontWeight.SemiBold)
                }

                if (loading) {
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            }

            // ======================================================
            // RESULTADOS + BOTONES
            // ======================================================
            if (resultados.isEmpty()) {
                item {
                    Text("No hay candidatos para mostrar", color = Color.Gray)

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            selectedVacante?.let { vac ->
                                vm.generarBrecha(vac, umbral.toInt())
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Crear brecha de skills")
                    }
                }
            } else {

                // Mostrar colaboradores
                itemsIndexed(resultados) { _, result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDetailFor = result.colaboradorId },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = primaryBlue.copy(alpha = 0.12f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = result.nombres.first().uppercase(),
                                        color = primaryBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(Modifier.weight(1f)) {
                                Text("${result.nombres} ${result.apellidos}", fontWeight = FontWeight.SemiBold)
                                Text("Match: ${"%.1f".format(result.puntaje)}%", color = Color.Gray)
                            }

                            if (result.disponibleParaMovilidad) {
                                Text("Disponible", color = Color(0xFF0C8E32))
                            }
                        }
                    }
                }

                // Botón para reporte PDF
                item {
                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            selectedVacante?.let { vac ->
                                vm.generarReporte(vac, resultados, umbral.toInt())
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(primaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Descargar reporte")
                    }

                    if (resultados.size <= 2) {
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                selectedVacante?.let { vac ->
                                    vm.generarBrecha(vac, umbral.toInt())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Crear brecha de skills")
                        }
                    }
                }
            }
        }
    }


    // ======================================================
// DIALOG DETALLE COLABORADOR
// ======================================================
    showDetailFor?.let { id ->
        val col = colaboradores.firstOrNull { it.id == id }
        if (col != null) {
            InfoDialogColaborador(col) {
                showDetailFor = null
            }
        }
    }

}

@Composable
fun InfoDialogColaborador(
    col: ColaboradorReadDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = {
            Text(
                "${col.nombres} ${col.apellidos}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Área: ${col.area}")
                Text("Rol: ${col.rolLaboral}")
                Text("Correo: ${col.correo}")
                Text("Estado: ${col.estado}")
                Spacer(Modifier.height(8.dp))

                Text("Skills técnicas:", fontWeight = FontWeight.SemiBold)
                val tecnicas = col.skills.filter { it.tipo.equals("TECNICO", true) }
                if (tecnicas.isEmpty()) {
                    Text("- Ninguna")
                } else {
                    tecnicas.forEach { s ->
                        Text("- ${s.nombre} (Nivel ${s.nivel})")
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text("Skills blandas:", fontWeight = FontWeight.SemiBold)
                val blandas = col.skills.filter { it.tipo.equals("BLANDO", true) }
                if (blandas.isEmpty()) {
                    Text("- Ninguna")
                } else {
                    blandas.forEach { s ->
                        Text("- ${s.nombre} (Nivel ${s.nivel})")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Certificaciones:", fontWeight = FontWeight.SemiBold)
                if (col.certificaciones.isEmpty()) {
                    Text("- Ninguna")
                } else {
                    col.certificaciones.forEach { c ->
                        Text("- ${c.nombre} — Vencimiento: ${c.fechaVencimiento ?: "-"}")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Disponible para movilidad: ${
                        if (col.disponibleParaMovilidad) "Sí" else "No"
                    }"
                )
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}


@Composable
fun InfoBlock(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0A63C2))
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoSectionVacante(vac: VacanteResponse) {
    Text("Área: ${vac.area}", fontWeight = FontWeight.Medium)
    Text("Urgencia: ${vac.urgencia ?: "-"}", fontWeight = FontWeight.Medium)

    Spacer(Modifier.height(8.dp))
    Text("Skills críticas", fontWeight = FontWeight.Bold)
    vac.skillsRequeridos.filter { it.esCritico }.forEach {
        Text("- ${it.nombre} (Nivel ${it.nivelDeseado})")
    }

    Spacer(Modifier.height(8.dp))
    Text("Skills no críticas", fontWeight = FontWeight.Bold)
    vac.skillsRequeridos.filter { !it.esCritico }.forEach {
        Text("- ${it.nombre} (Nivel ${it.nivelDeseado})")
    }

    Spacer(Modifier.height(8.dp))
    Text("Certificaciones requeridas", fontWeight = FontWeight.Bold)
    vac.certificacionesRequeridas.forEach {
        Text("- $it")
    }
}
