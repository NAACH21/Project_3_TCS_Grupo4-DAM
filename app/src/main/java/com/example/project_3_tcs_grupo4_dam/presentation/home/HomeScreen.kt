package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // 1. Obtenemos la instancia del ViewModel.
    // Compose se encarga de su ciclo de vida.
    val viewModel: HomeViewModel = viewModel()

    // 2. Observamos los StateFlows y los convertimos en Estado de Compose
    val colaboradores by viewModel.colaboradores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 3. Definimos la UI
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Colaboradores") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error ?: "Error", color = MaterialTheme.colorScheme.error)
                colaboradores.isEmpty() -> Text("Sin datos por mostrar", style = MaterialTheme.typography.bodyMedium)
                else -> ColaboradorList(colaboradores)
            }
        }
    }
}

@Composable
fun ColaboradorList(colaboradores: List<ColaboradorReadDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(colaboradores) { colaborador ->
            ColaboradorItem(colaborador)
        }
    }
}

@Composable
fun ColaboradorItem(colaborador: ColaboradorReadDto) {
    // Un item simple, puedes mejorarlo
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "${colaborador.nombres} ${colaborador.apellidos}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Rol: ${colaborador.rolActual}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Skill: ${colaborador.skillPrimario}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}