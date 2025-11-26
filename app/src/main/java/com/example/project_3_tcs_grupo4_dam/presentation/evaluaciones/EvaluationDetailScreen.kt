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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillEvaluadoDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationDetailScreen(
    evaluationId: String,
    onBack: () -> Unit,
    viewModel: EvaluationDetailViewModel = viewModel()
) {
    val evaluation by viewModel.evaluation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(evaluationId) {
        viewModel.loadEvaluation(evaluationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Evaluación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(text = error!!, color = MaterialTheme.colorScheme.error)
                evaluation == null -> Text("No se encontró la evaluación.")
                else -> EvaluationDetails(evaluation!!)
            }
        }
    }
}

@Composable
private fun EvaluationDetails(evaluation: EvaluacionReadDto) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            EvaluationInfoCard(evaluation)
        }
        item {
            Text(
                text = "Skills Evaluados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        items(evaluation.skillsEvaluados) { skill ->
            SkillDetailCard(skill)
        }
        item {
            CommentsCard(evaluation.comentarios)
        }
    }
}

@Composable
private fun EvaluationInfoCard(evaluation: EvaluacionReadDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow("Colaborador ID:", evaluation.colaboradorId)
            InfoRow("Rol Actual:", evaluation.rolActual)
            InfoRow("Líder Evaluador:", evaluation.liderEvaluador)
            InfoRow("Fecha:", evaluation.fechaEvaluacion.substringBefore("T"))
            InfoRow("Tipo:", evaluation.tipoEvaluacion)
            InfoRow("Responsable:", evaluation.usuarioResponsable)
        }
    }
}

@Composable
private fun SkillDetailCard(skill: SkillEvaluadoDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = skill.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            InfoRow("Tipo:", skill.tipo)
            InfoRow("Nivel Actual:", skill.nivelActual.toString())
            InfoRow("Nivel Recomendado:", skill.nivelRecomendado.toString())
        }
    }
}

@Composable
private fun CommentsCard(comments: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Comentarios Generales", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(comments)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label ", fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}
