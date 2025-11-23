package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionDto
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorDetalleScreen(
    colaboradorId: String,
    onBack: () -> Unit
) {
    // CORRECCIÓN CRÍTICA: Usar la Factory personalizada para pasar colaboradorId al ViewModel
    val viewModel: ColaboradorDetalleViewModel = viewModel(
        factory = ColaboradorDetalleViewModelFactory(colaboradorId)
    )

    val colaborador by viewModel.colaborador.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Colaborador") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                colaborador == null -> {
                    Text("Sin datos del colaborador")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header del colaborador
                        item {
                            ColaboradorHeader(colaborador!!)
                        }

                        // Sección de Skills
                        item {
                            SkillsSection(nivelCodigo = colaborador!!.nivelCodigo)
                        }

                        // Sección de Certificaciones
                        item {
                            CertificacionesSection(certificaciones = colaborador!!.certificaciones)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColaboradorHeader(colaborador: com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con iniciales
            val iniciales = (colaborador.nombres.firstOrNull()?.toString() ?: "") +
                    (colaborador.apellidos.firstOrNull()?.toString() ?: "")

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre completo
            Text(
                text = "${colaborador.nombres} ${colaborador.apellidos}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rol actual
            Text(
                text = colaborador.rolActual,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Área (CORREGIDO: Manejo de nulo)
            Text(
                text = colaborador.area ?: "Sin área",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips de disponibilidad (CORREGIDO: Manejo de nulo)
            val disponibilidad = colaborador.disponibilidad
            if (disponibilidad != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val estado = disponibilidad.estado ?: "No especificado"
                    AssistChip(
                        onClick = {},
                        label = { Text(estado) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (estado == "Disponible")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        )
                    )
                    
                    val dias = disponibilidad.dias ?: 0
                    AssistChip(
                        onClick = {},
                        label = { Text("$dias días") }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillsSection(nivelCodigo: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Skills Técnicos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val nivelTexto = when (nivelCodigo) {
                0 -> "No iniciado"
                1 -> "Básico"
                2 -> "Intermedio"
                3 -> "Avanzado"
                else -> "No especificado"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nivel de código:",
                    style = MaterialTheme.typography.bodyLarge
                )
                AssistChip(
                    onClick = {},
                    label = { Text(nivelTexto) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (nivelCodigo) {
                            3 -> MaterialTheme.colorScheme.primaryContainer
                            2 -> MaterialTheme.colorScheme.secondaryContainer
                            1 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun CertificacionesSection(certificaciones: List<CertificacionDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Certificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (certificaciones.isEmpty()) {
                Text(
                    text = "Sin certificaciones registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                certificaciones.forEach { cert ->
                    CertificacionItem(cert)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CertificacionItem(certificacion: CertificacionDto) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CORREGIDO: Nombre puede ser nulo
                Text(
                    text = certificacion.nombre ?: "Sin nombre",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                // CORREGIDO: Estado puede ser nulo
                val estado = certificacion.estado ?: "Desconocido"
                AssistChip(
                    onClick = {},
                    label = { Text(estado) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (estado.lowercase() == "vigente")
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                )
            }

            certificacion.fechaObtencion?.let { fecha ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Obtenida: ${formatearFecha(fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        date?.let { formatoSalida.format(it) } ?: fecha
    } catch (_: Exception) {
        fecha
    }
}
