package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador.ColaboradorCard
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorListScreen(navController: NavController) {
    val viewModel: ColaboradorListViewModel = viewModel()
    val colaboradores by viewModel.colaboradores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Colaboradores") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ColaboradorFormScreen.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir colaborador")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(colaboradores) { colaborador ->
                        ColaboradorCard(
                            colaborador = colaborador,
                            onVerDetalle = {
                                navController.navigate(Routes.ColaboradorDetailScreen.withId(colaborador.id))
                            },
                            onEditar = {
                                navController.navigate(Routes.ColaboradorFormScreen.route + "?colaboradorId=${colaborador.id}")
                            },
                            onEliminar = {
                                viewModel.deleteColaborador(colaborador.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
