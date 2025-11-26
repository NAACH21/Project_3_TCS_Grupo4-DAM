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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationScreen(
    navController: NavController,
    viewModel: EvaluationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            EvaluationTopAppBar(
                onBackClick = { navController.popBackStack() },
                onHistoryClick = { navController.navigate("evaluations_history") },
                onBulkLoadClick = { navController.navigate("bulk_upload") }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
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
                    onSave = viewModel::saveEvaluation
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
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
                Text("Historial")
            }
            TextButton(onClick = onBulkLoadClick) {
                Text("Carga masiva")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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

            CustomDropdown(
                label = "Colaborador *",
                options = uiState.collaboratorOptions,
                selectedValue = uiState.collaborator,
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
                label = { Text("Fecha *") },
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Agregar")
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
fun BottomActionButtons(onCancel: () -> Unit, onSave: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar", color = Color.White)
            }
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar evaluación")
            }
        }
    }
}
