package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationsHistoryScreen(
    navController: NavController,
    embedded: Boolean = false,
    onNavigateToDetail: (String) -> Unit,
    viewModel: EvaluationsHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (embedded) {
        // Modo embebido: solo contenido interno
        EvaluationsHistoryContent(
            uiState = uiState,
            viewModel = viewModel,
            navController = navController,
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Modo standalone: con Scaffold completo
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("TCS HR Manager") },
                        actions = {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                            }
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.Person, contentDescription = "Perfil")
                            }
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Volver")
                        }
                        Button(onClick = { /* TODO: Export PDF */ }) {
                            Text("Exportar PDF")
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    homeRoute = Routes.ADMIN_HOME
                )
            }
        ) { padding ->
            EvaluationsHistoryContent(
                uiState = uiState,
                viewModel = viewModel,
                navController = navController,
                onNavigateToDetail = onNavigateToDetail,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun EvaluationsHistoryContent(
    uiState: EvaluationsHistoryUiState,
    viewModel: EvaluationsHistoryViewModel,
    navController: NavController,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DescriptionCard() }
        item { FilterCard(uiState = uiState, viewModel = viewModel) }
        item { 
            Text(
                text = "Evaluaciones registradas (${uiState.filteredEvaluations.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(uiState.filteredEvaluations) { item ->
            EvaluationHistoryItemCard(item = item, onNavigateToDetail = onNavigateToDetail)
        }
    }
}

@Composable
fun DescriptionCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Historial de evaluaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Visualiza y gestiona todas las evaluaciones registradas en el sistema.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FilterCard(uiState: EvaluationsHistoryUiState, viewModel: EvaluationsHistoryViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por colaborador") },
                placeholder = { Text("Nombre del colaborador") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomDropdown(
                label = "Tipo de evaluación",
                options = uiState.evaluationTypes,
                selectedValue = uiState.selectedType,
                onValueChange = viewModel::onEvaluationTypeChange
            )
        }
    }
}

@Composable
fun EvaluationHistoryItemCard(item: EvaluationHistoryItem, onNavigateToDetail: (String) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(item.collaboratorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
                    Text(item.level, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(item.role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${item.evaluationType} • ${item.date}", style = MaterialTheme.typography.bodySmall)
            Text("Skills: ${item.skills.joinToString()}", style = MaterialTheme.typography.bodySmall)
            Text("Cargado por: ${item.loadedBy}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onNavigateToDetail(item.id) }, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver detalles")
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
