package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkUploadScreen(
    onBackClick: () -> Unit,
    viewModel: BulkUploadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            val fileName = uri?.let { getFileName(context, it) }
            viewModel.onFileSelected(uri, fileName)
        }
    )

    // Handle upload status dialogs
    HandleUploadStatus(uiState = uiState, onDismiss = viewModel::onStatusConsumed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carga Masiva de Evaluaciones") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.processFile(context) },
                enabled = uiState.fileUri != null && !uiState.isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                } else {
                    Text("Procesar archivo")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            CsvTemplateCard(onDownload = viewModel::downloadTemplate)

            FileUploadCard(
                uiState = uiState,
                onFileSelect = { filePickerLauncher.launch("text/csv") }
            )
        }
    }
}

@Composable
private fun HandleUploadStatus(uiState: BulkUploadUiState, onDismiss: () -> Unit) {
    if (uiState.uploadSuccess) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Éxito") },
            text = { Text("El archivo ha sido procesado y las evaluaciones han sido creadas.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Aceptar") } }
        )
    }

    uiState.uploadError?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Error") },
            text = { Text(it) },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
        )
    }
}


@Composable
fun CsvTemplateCard(onDownload: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                "Plantilla CSV",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "El archivo debe ser formato .csv y contener las siguientes columnas:",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                """
• Colaborador (nombre completo)
• Rol actual (nombre del rol)
• Líder evaluador (nombre del líder)
• Fecha evaluación (YYYY-MM-DD)
• Skills (habilidades evaluadas)
• Nivel actual (Básico, Intermedio, Avanzado)
• Nivel recomendado (Básico, Intermedio, Avanzado)
• Tipo de evaluación
• Comentarios (opcional)
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onDownload, modifier = Modifier.align(Alignment.End)) {
                Text("Descargar plantilla CSV")
            }
        }
    }
}

@Composable
fun FileUploadCard(uiState: BulkUploadUiState, onFileSelect: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Cargar archivo de evaluaciones", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Seleccione un archivo .csv con las evaluaciones de los colaboradores.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.fileUri == null) {
                FileUploadBox(onClick = onFileSelect)
            } else {
                FileSelectedBox(uiState.fileName ?: "Archivo seleccionado")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onFileSelect, 
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isProcessing
            ) {
                Text("Seleccionar otro archivo")
            }
        }
    }
}

@Composable
fun FileUploadBox(onClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecciona un archivo CSV con las evaluaciones", textAlign = TextAlign.Center)
    }
}

@Composable
fun FileSelectedBox(fileName: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.Description, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(fileName, fontWeight = FontWeight.SemiBold)
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) fileName = cursor.getString(nameIndex)
        }
    }
    return fileName
}
