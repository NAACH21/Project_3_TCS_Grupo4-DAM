package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Saludo
            Text(
                text = "Hola, Leslie 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bienvenida al sistema de gestión de talento",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de métricas (muy simples por ahora)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(title = "Colaboradores activos", value = "142")
                MetricCard(title = "Evaluaciones pendientes", value = "8")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(title = "Vacantes abiertas", value = "5")
                MetricCard(title = "% Cobertura de skills", value = "78%")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones rápidos (solo UI, todavía sin navegación extra)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("colaboradores") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Colaboradores")
                }
                Button(onClick = { /* TODO Matching */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Matching Inteligente")
                }
                Button(onClick = { /* TODO Dashboard */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Dashboard General")
                }
                Button(onClick = { /* TODO Brechas */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Brechas de Skills")
                }
                Button(onClick = { /* TODO Alertas */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Alertas Automáticas")
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = modifier
            .height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
