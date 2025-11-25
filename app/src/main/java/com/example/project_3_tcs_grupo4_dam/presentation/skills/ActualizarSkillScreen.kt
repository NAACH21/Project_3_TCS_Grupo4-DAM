package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar

// Colores personalizados
private val TCSBlue = Color(0xFF00549F)
private val LightBlueBg = Color(0xFFE3F2FD)
private val GrayText = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarSkillScreen(
    navController: NavController,
    colaboradorId: String,
    skillName: String
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Instanciamos el ViewModel con su Factory personalizado
    val viewModel: ActualizarSkillViewModel = viewModel(
        factory = ActualizarSkillViewModelFactory(
            sessionManager = sessionManager,
            colaboradorId = colaboradorId,
            skillName = skillName
        )
    )

    // Recolectamos el estado del StateFlow
    val skillState = viewModel.skillState.collectAsState()
    val skill = skillState.value
    
    // Observamos mensajes de error
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            Toast.makeText(context, "Solicitud enviada para validación", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualizar Skill", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            ColaboradorBottomNavBar(navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (viewModel.isLoading && skill == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TCSBlue)
                }
            } else if (skill != null) {
                ContentActualizarSkill(
                    skill = skill,
                    viewModel = viewModel,
                    onCancel = { navController.popBackStack() }
                )
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentActualizarSkill(
    skill: SkillReadDto,
    viewModel: ActualizarSkillViewModel,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = skill.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = TCSBlue,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Sección Información Actual
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightBlueBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Información Actual", style = MaterialTheme.typography.labelLarge, color = GrayText)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Tipo", style = MaterialTheme.typography.bodySmall, color = GrayText)
                        Text(skill.tipo, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Nivel Actual", style = MaterialTheme.typography.bodySmall, color = GrayText)
                        Text(getNivelLabel(skill.nivel), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Sección Nuevo Nivel
        Text("Nuevo Nivel Solicitado", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        var expanded by remember { mutableStateOf(false) }
        val niveles = listOf(1, 2, 3, 4).filter { it > viewModel.currentNivel } // Solo niveles superiores
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = getNivelLabel(viewModel.selectedNivel),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                niveles.forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(getNivelLabel(nivel)) },
                        onClick = {
                            viewModel.selectedNivel = nivel
                            expanded = false
                        }
                    )
                }
            }
        }
        Text(
            "Selecciona el nivel que deseas validar.",
            style = MaterialTheme.typography.bodySmall,
            color = GrayText,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Tipo de Evidencia
        Text("Tipo de Evidencia", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, TCSBlue, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            SegmentedButton(
                text = "URL",
                icon = Icons.Default.Link,
                isSelected = viewModel.selectedTipoEvidencia == "URL",
                onClick = { viewModel.selectedTipoEvidencia = "URL" },
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(TCSBlue))
            SegmentedButton(
                text = "Archivo",
                icon = Icons.Default.UploadFile,
                isSelected = viewModel.selectedTipoEvidencia == "ARCHIVO",
                onClick = { viewModel.selectedTipoEvidencia = "ARCHIVO" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Input Evidencia
        if (viewModel.selectedTipoEvidencia == "URL") {
            Text("URL de Evidencia", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = viewModel.urlEvidencia,
                onValueChange = { viewModel.urlEvidencia = it },
                placeholder = { Text("https://ejemplo.com/certificado.pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            Text(
                "Enlace a certificación, proyecto, portafolio, etc.",
                style = MaterialTheme.typography.bodySmall,
                color = GrayText
            )
        } else {
            // UI Mock para selector de archivo
            OutlinedButton(
                onClick = { /* TODO: Implementar file picker */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar archivo (No implementado)")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Notas Adicionales
        Text("Notas Adicionales (Opcional)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.notasAdicionales,
            onValueChange = { viewModel.notasAdicionales = it },
            placeholder = { Text("Ej: Experiencia liderando proyecto X...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Botones de Acción
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = { viewModel.enviarValidacion() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TCSBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = viewModel.selectedNivel > viewModel.currentNivel && viewModel.urlEvidencia.isNotBlank()
            ) {
                Text("Enviar para Validación", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun SegmentedButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) LightBlueBg else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = text, tint = if (isSelected) TCSBlue else GrayText)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = if (isSelected) TCSBlue else GrayText, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

private fun getNivelLabel(nivel: Int): String {
    return when (nivel) {
        1 -> "Básico"
        2 -> "Intermedio"
        3 -> "Avanzado"
        4 -> "Experto"
        else -> "Nivel $nivel"
    }
}
