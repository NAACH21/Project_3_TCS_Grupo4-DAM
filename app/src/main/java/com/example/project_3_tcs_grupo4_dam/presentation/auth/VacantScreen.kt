// VacantesScreen.kt
package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantScreen(navController: NavController) {

    val vacantes = remember {
        listOf(
            Vacante(
                titulo = "Desarrollador Full Stack Senior",
                area = "Tecnología",
                perfil = "Desarrollador",
                estado = EstadoVacante.ACTIVA,
                urgencia = Urgencia.ALTA
            ),
            Vacante(
                titulo = "Analista de Business Intelligence",
                area = "Business Intelligence",
                perfil = "Analista",
                estado = EstadoVacante.ACTIVA,
                urgencia = Urgencia.MEDIA
            ),
            Vacante(
                titulo = "Project Manager",
                area = "Gestión de Proyectos",
                perfil = "Gerente",
                estado = EstadoVacante.OCUPADA,
                urgencia = Urgencia.BAJA
            ),
            Vacante(
                titulo = "UX/UI Designer",
                area = "Diseño",
                perfil = "Diseñador",
                estado = EstadoVacante.ACTIVA,
                urgencia = Urgencia.MEDIA
            )
        )
    }

    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacantes", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1959B8)
                ),
                actions = {
                    // SOLO campanita, sin icono de persona
                    IconButton(onClick = { /* TODO: notificaciones */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            tint = Color.White,
                            contentDescription = "Notificaciones"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBarVacantes()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {

            // Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                placeholder = { Text("Buscar por perfil o área...") },
                shape = RoundedCornerShape(50)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de filtros
            OutlinedButton(
                onClick = { /* TODO: filtros */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Filtros (Área, Estado, Urgencia)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Nueva Vacante
            Button(
                onClick = { navController.navigate("newVacancy") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Nueva Vacante")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de tarjetas
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(vacantes) { vacante ->
                    VacanteCard(vacante = vacante)
                }
            }
        }
    }
}



@Composable
fun VacanteCard(vacante: Vacante) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = vacante.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = vacante.area,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = vacante.perfil,
                fontSize = 13.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoChip(vacante.estado)
                UrgenciaChip(vacante.urgencia)
            }
        }
    }
}

@Composable
fun EstadoChip(estado: EstadoVacante) {
    val (bg, textColor, label) = when (estado) {
        EstadoVacante.ACTIVA -> Triple(Color(0xFFE5EDFF), Color(0xFF2563EB), "Activa")
        EstadoVacante.OCUPADA -> Triple(Color(0xFFE6FFE9), Color(0xFF16A34A), "Ocupada")
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 11.sp)
    }
}

@Composable
fun UrgenciaChip(urgencia: Urgencia) {
    val (bg, textColor, label) = when (urgencia) {
        Urgencia.ALTA -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Urgencia: Alta")
        Urgencia.MEDIA -> Triple(Color(0xFFFFF3CD), Color(0xFFB45309), "Urgencia: Media")
        Urgencia.BAJA -> Triple(Color(0xFFEFFDEB), Color(0xFF15803D), "Urgencia: Baja")
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 11.sp)
    }
}

// --- Bottom navigation (estático, solo diseño) ---
@Composable
fun BottomBarVacantes() {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            label = { Text("Inicio") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            label = { Text("Colaboradores") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            label = { Text("Evaluaciones") },
            icon = {}
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* ya estamos aquí */ },
            label = { Text("Vacantes") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            label = { Text("Dashboard") },
            icon = {}
        )
    }
}

data class Vacante(
    val titulo: String,
    val area: String,
    val perfil: String,
    val estado: EstadoVacante,
    val urgencia: Urgencia
)

enum class EstadoVacante { ACTIVA, OCUPADA }
enum class Urgencia { ALTA, MEDIA, BAJA }


