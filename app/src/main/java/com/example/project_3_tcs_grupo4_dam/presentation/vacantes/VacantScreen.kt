// VacantScreen.kt
package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar

// ======================================================
// DATA CLASS PARA ADMIN
// ======================================================
data class Vacante(
    val titulo: String,
    val area: String,
    val perfil: String,
    val estado: EstadoVacante,
    val urgencia: Urgencia
)

enum class EstadoVacante {
    ACTIVA, OCUPADA, CERRADA
}

enum class Urgencia {
    ALTA, MEDIA, BAJA
}

// ======================================================
// PANTALLA PARA ADMINISTRADOR
// ======================================================
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
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("newVacancy") },
                containerColor = Color(0xFF1959B8)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva vacante",
                    tint = Color.White
                )
            }
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                placeholder = { Text("Buscar vacante...") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de vacantes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vacantes) { vacante ->
                    VacanteAdminCard(vacante)
                }
            }
        }
    }
}

// ======================================================
// CARD COMPONENT PARA ADMIN
// ======================================================
@Composable
fun VacanteAdminCard(vacante: Vacante) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = vacante.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Badge de urgencia
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (vacante.urgencia) {
                        Urgencia.ALTA -> Color(0xFFFFEBEE)
                        Urgencia.MEDIA -> Color(0xFFFFF3E0)
                        Urgencia.BAJA -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = vacante.urgencia.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = when (vacante.urgencia) {
                            Urgencia.ALTA -> Color(0xFFC62828)
                            Urgencia.MEDIA -> Color(0xFFEF6C00)
                            Urgencia.BAJA -> Color(0xFF2E7D32)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${vacante.area} • ${vacante.perfil}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estado
            Row {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (vacante.estado) {
                        EstadoVacante.ACTIVA -> Color(0xFF1976D2)
                        EstadoVacante.OCUPADA -> Color(0xFF388E3C)
                        EstadoVacante.CERRADA -> Color(0xFF757575)
                    }
                ) {
                    Text(
                        text = vacante.estado.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
