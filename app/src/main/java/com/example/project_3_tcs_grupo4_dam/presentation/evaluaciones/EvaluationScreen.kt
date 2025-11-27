package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationScreen(
    navController: NavController,
    viewModel: EvaluationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show loading, success or error dialogs
    HandleSaveStatus(uiState = uiState, viewModel = viewModel) { navController.popBackStack() }

    Scaffold(
        topBar = {
            EvaluationTopAppBar(
                onBackClick = { navController.popBackStack() },
                onHistoryClick = { navController.navigate(Routes.EvaluationHistoryScreen.route) },
                onBulkLoadClick = { navController.navigate(Routes.BulkUploadScreen.route) }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    EvaluationDataCard(uiState = uiState, viewModel = viewModel)
                }

                item {
                    EvaluatedSkillsSection(viewModel = viewModel)
                }

                items(uiState.skills, key = { it.id }) { skill ->
                    SkillItemCard(
                        skill = skill,
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Espacio para los botones de acción
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    BottomActionButtons(
                        onCancel = viewModel::cancelEvaluation,
                        onSave = viewModel::saveEvaluation,
                        isSaving = uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HandleSaveStatus(uiState: EvaluationUiState, viewModel: EvaluationViewModel, onSaveSuccess: () -> Unit) {
    if (uiState.saveSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onSaveStatusConsumed()
                onSaveSuccess() // Navigate back on success
            },
            title = { Text("Éxito") },
            text = { Text("La evaluación se ha guardado correctamente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onSaveStatusConsumed()
                        onSaveSuccess()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    uiState.saveError?.let {
        AlertDialog(
            onDismissRequest = { viewModel.onSaveStatusConsumed() },
            title = { Text("Error") },
            text = { Text("No se pudo guardar la evaluación: $it") },
            confirmButton = {
                TextButton(onClick = { viewModel.onSaveStatusConsumed() }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationTopAppBar(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBulkLoadClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Evaluaciones", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retroceder")
            }
        },
        actions = {
            TextButton(onClick = onHistoryClick) {
                Text("Historial", color = Color.White)
            }
            TextButton(onClick = onBulkLoadClick) {
                Text("Carga masiva", color = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0A63C2), // Azul oscuro
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun EvaluationDataCard(uiState: EvaluationUiState, viewModel: EvaluationViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DATOS DE LA EVALUACIÓN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CollaboratorDropdown(
                label = "Colaborador *",
                collaborators = uiState.collaboratorOptions,
                selectedCollaboratorId = uiState.selectedCollaboratorId,
                onValueChange = viewModel::onCollaboratorChange
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.currentRole,
                onValueChange = {},
                label = { Text("Rol actual") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.evaluatorLeader,
                onValueChange = viewModel::onEvaluatorLeaderChange,
                label = { Text("Líder evaluador *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fake Date Picker
            OutlinedTextField(
                value = uiState.evaluationDate,
                onValueChange = viewModel::onDateChange,
                label = { Text("Fecha (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomDropdown(
                label = "Tipo de evaluación *",
                options = uiState.evaluationTypeOptions,
                selectedValue = uiState.evaluationType,
                onValueChange = viewModel::onEvaluationTypeChange
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.comments,
                onValueChange = viewModel::onCommentsChange,
                label = { Text("Comentarios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )
        }
    }
}

@Composable
fun EvaluatedSkillsSection(viewModel: EvaluationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Skills evaluados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = viewModel::addSkill,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A63C2)) // Azul oscuro
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Agregar", color = Color.White)
        }
    }
}

@Composable
fun SkillItemCard(
    skill: Skill,
    uiState: EvaluationUiState,
    viewModel: EvaluationViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = skill.name,
                    onValueChange = { newValue ->
                        viewModel.onSkillChange(skill.id) { it.copy(name = newValue) }
                    },
                    label = { Text("Nombre del skill") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.removeSkill(skill.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar skill", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            CustomDropdown(
                label = "Técnico / Soft",
                options = uiState.skillTypeOptions,
                selectedValue = skill.type,
                onValueChange = { newValue ->
                    viewModel.onSkillChange(skill.id) { it.copy(type = newValue) }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomDropdown(
                label = "Nivel actual",
                options = uiState.skillLevelOptions,
                selectedValue = skill.currentLevel,
                onValueChange = { newValue ->
                    viewModel.onSkillChange(skill.id) { it.copy(currentLevel = newValue) }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomDropdown(
                label = "Nivel recomendado",
                options = uiState.skillLevelOptions,
                selectedValue = skill.recommendedLevel,
                onValueChange = { newValue ->
                    viewModel.onSkillChange(skill.id) { it.copy(recommendedLevel = newValue) }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = skill.comments,
                onValueChange = { newValue ->
                    viewModel.onSkillChange(skill.id) { it.copy(comments = newValue) }
                },
                label = { Text("Comentarios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollaboratorDropdown(
    label: String,
    collaborators: List<ColaboradorReadDto>,
    selectedCollaboratorId: String?,
    onValueChange: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val selectedCollaborator = collaborators.find { it.id == selectedCollaboratorId }
    val selectedText = selectedCollaborator?.let { "${it.nombres} ${it.apellidos}" } ?: ""

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            collaborators.forEach { collaborator ->
                DropdownMenuItem(
                    text = { Text("${collaborator.nombres} ${collaborator.apellidos}") },
                    onClick = {
                        onValueChange(collaborator.id)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onValueChange(it)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BottomActionButtons(onCancel: () -> Unit, onSave: () -> Unit, isSaving: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A63C2)), // Azul oscuro
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar evaluación", color = Color.White)
                }
            }
        }
    }
}
