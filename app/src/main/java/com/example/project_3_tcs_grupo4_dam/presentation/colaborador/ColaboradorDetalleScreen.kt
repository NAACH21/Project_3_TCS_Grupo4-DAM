package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionReadDto
import java.text.SimpleDateFormat
import java.util.*

// 🔹 Color Corporativo TCS
private val PrimaryBlue = Color(0xFF00549F)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorDetalleScreen(
    colaboradorId: String,
    onBack: () -> Unit
) {
    val viewModel: ColaboradorDetalleViewModel = viewModel()

    val colaborador by viewModel.colaborador.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    val certificadoUrlToOpen by viewModel.certificadoUrlToOpen.collectAsState()
    val certificadoError by viewModel.certificadoError.collectAsState()

    // Estados para download de PDF con FileProvider
    val isDownloadingPdf by viewModel.isDownloadingPdf.collectAsState()
    val pdfFileToOpen by viewModel.pdfFileToOpen.collectAsState()
    val pdfErrorMessage by viewModel.pdfErrorMessage.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Abrir PDF cuando hay una URL disponible (bucket público)
    LaunchedEffect(certificadoUrlToOpen) {
        certificadoUrlToOpen?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                viewModel.onCertificadoUrlConsumed()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error al abrir PDF: ${e.message}")
                viewModel.onCertificadoUrlConsumed()
            }
        }
    }

    // Mostrar error de certificado si existe
    LaunchedEffect(certificadoError) {
        certificadoError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearCertificadoError()
        }
    }

    // Abrir PDF descargado con FileProvider (bucket privado)
    LaunchedEffect(pdfFileToOpen) {
        val file = pdfFileToOpen ?: return@LaunchedEffect

        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
            viewModel.onPdfOpened()

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No hay aplicación para abrir PDFs",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onPdfOpened()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error al abrir PDF: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onPdfOpened()
        }
    }

    // Mostrar error de descarga de PDF si existe
    LaunchedEffect(pdfErrorMessage) {
        pdfErrorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearPdfError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detalle del Colaborador",
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(color = PrimaryBlue)
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
                            SkillsSection(skills = colaborador!!.skills)
                        }

                        // Sección de Certificaciones
                        item {
                            CertificacionesSection(
                                certificaciones = colaborador!!.certificaciones,
                                onVerPdfClick = { archivoPdfUrl ->
                                    // Usar descarga con FileProvider (bucket privado)
                                    viewModel.onVerCertificadoPdfClick(archivoPdfUrl)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dialog de loading mientras se descarga el PDF
        if (isDownloadingPdf) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = { },
                title = { Text("Descargando PDF") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), color = PrimaryBlue)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Por favor espera...")
                    }
                }
            )
        }
    }
}

@Composable
private fun ColaboradorHeader(colaborador: ColaboradorReadDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre completo
            Text(
                text = "${colaborador.nombres} ${colaborador.apellidos}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rol laboral
            Text(
                text = colaborador.rolLaboral,
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Área
            Text(
                text = colaborador.area,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips de estado y disponibilidad para movilidad
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip Estado
                val isActivo = colaborador.estado.equals("ACTIVO", ignoreCase = true)
                val bgEstado = if (isActivo) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                val textEstado = if (isActivo) Color(0xFF2E7D32) else Color(0xFFC62828)
                
                Surface(
                    shape = CircleShape,
                    color = bgEstado,
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(text = colaborador.estado, color = textEstado, fontWeight = FontWeight.Bold)
                    }
                }

                // Chip Disponibilidad
                val isDisponible = colaborador.disponibleParaMovilidad
                val bgDisp = if (isDisponible) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                val textDisp = if (isDisponible) PrimaryBlue else Color.Gray
                
                Surface(
                    shape = CircleShape,
                    color = bgDisp,
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(
                            text = if (isDisponible) "Disponible" else "No disponible", 
                            color = textDisp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillsSection(skills: List<SkillReadDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (skills.isEmpty()) {
                Text(
                    text = "Sin skills registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                skills.forEach { skill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = skill.nombre,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = skill.tipo,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = "Nivel ${skill.nivel}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = PrimaryBlue,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            if (skill.esCritico) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFEBEE)
                                ) {
                                    Text(
                                        text = "Crítico",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = Color(0xFFC62828),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}

@Composable
private fun CertificacionesSection(
    certificaciones: List<CertificacionReadDto>,
    onVerPdfClick: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Certificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (certificaciones.isEmpty()) {
                Text(
                    text = "Sin certificaciones registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                certificaciones.forEach { cert ->
                    CertificacionItem(
                        certificacion = cert,
                        onVerPdfClick = onVerPdfClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CertificacionItem(
    certificacion: CertificacionReadDto,
    onVerPdfClick: (String?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.small,
        color = Color(0xFFFAFAFA),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = certificacion.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = certificacion.institucion,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                val isVigente = certificacion.estado.equals("vigente", ignoreCase = true)
                Surface(
                    shape = CircleShape,
                    color = if (isVigente) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = certificacion.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isVigente) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            certificacion.fechaObtencion?.let { fecha ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Obtenida: ${formatearFecha(fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            certificacion.fechaVencimiento?.let { fecha ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vence: ${formatearFecha(fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Botón para ver PDF
            Spacer(modifier = Modifier.height(12.dp))

            if (certificacion.archivoPdfUrl.isNullOrBlank()) {
                Text(
                    text = "Sin archivo PDF subido",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else {
                OutlinedButton(
                    onClick = { onVerPdfClick(certificacion.archivoPdfUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text("Ver PDF")
                }
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
