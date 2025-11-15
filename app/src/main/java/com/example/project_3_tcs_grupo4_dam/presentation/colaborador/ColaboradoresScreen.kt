package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador.ColaboradorCard
import com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador.ColaboradoresHeader
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradoresScreen(navController: NavController) {
    // ViewModel de esta pantalla
    val viewModel: ColaboradoresViewModel = viewModel()

    val colaboradores by viewModel.colaboradores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Colaboradores") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔹 Header con búsqueda / filtros / carga masiva / nuevo colaborador
            ColaboradoresHeader(
                onNuevoColaborador = { /* TODO: Navegar a pantalla de registro */ },
                onCargaMasiva = { /* TODO: Abrir flujo de carga masiva */ },
                onFiltros = { /* TODO: Mostrar filtros */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Contenido principal (lista, loading, error)
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    colaboradores.isEmpty() -> {
                        Text("Sin colaboradores por mostrar")
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(colaboradores) { colaborador ->
                                ColaboradorCard(
                                    colaborador = colaborador,
                                    onVerDetalle = {
                                        navController.navigate("${Routes.COLABORADOR_DETALLE}/${colaborador.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
