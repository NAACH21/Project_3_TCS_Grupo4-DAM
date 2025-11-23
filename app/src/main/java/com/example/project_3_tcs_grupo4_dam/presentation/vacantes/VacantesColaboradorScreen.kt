package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar

// ==========================================
// DATA MODEL
// ==========================================
data class VacanteColaboradorDto(
    val id: Int,
    val titulo: String,
    val especialidad: String,
    val ubicacion: String,
    val urgencia: String, // "Alta", "Media", "Baja"
    val match: Int, // 0 - 100
    val skills: List<Pair<String, String>>, // Nombre - Nivel
    val descripcion: String
)

// ==========================================
// MOCK DATA
// ==========================================
val mockVacantesColaborador = listOf(
    VacanteColaboradorDto(
        id = 1,
        titulo = "Senior React Developer",
        especialidad = "Frontend Development",
        ubicacion = "Lima, Perú (Híbrido)",
        urgencia = "Alta",
        match = 95,
        skills = listOf("React" to "Avanzado", "TypeScript" to "Avanzado", "Redux" to "Intermedio"),
        descripcion = "Buscamos un experto en React para liderar la migración de nuestra plataforma bancaria. Se requiere experiencia en arquitectura de componentes."
    ),
    VacanteColaboradorDto(
        id = 2,
        titulo = "Backend Node.js Engineer",
        especialidad = "Backend Services",
        ubicacion = "Remoto",
        urgencia = "Media",
        match = 78,
        skills = listOf("Node.js" to "Avanzado", "AWS" to "Intermedio", "MongoDB" to "Avanzado"),
        descripcion = "Únete al equipo de microservicios. Trabajarás en APIs de alto rendimiento y escalabilidad para clientes internacionales."
    ),
    VacanteColaboradorDto(
        id = 3,
        titulo = "DevOps Specialist",
        especialidad = "Infraestructura",
        ubicacion = "Lima, Perú",
        urgencia = "Baja",
        match = 45,
        skills = listOf("Docker" to "Avanzado", "Kubernetes" to "Básico", "Jenkins" to "Intermedio"),
        descripcion = "Encargado de mantener los pipelines de CI/CD y asegurar la disponibilidad de los entornos de producción."
    )
)

// ==========================================
// SCREEN PRINCIPAL
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantesColaboradorScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacantes para Ti", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            // USAMOS EL BOTTOM BAR DE COLABORADOR
            ColaboradorBottomNavBar(navController = navController)
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                placeholder = { Text("Buscar vacantes...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            // FILTROS SUPERIORES
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                FilterChipMatch("Todos", filtroSeleccionado == "Todos") { filtroSeleccionado = "Todos" }
                FilterChipMatch("Urgencia Alta", filtroSeleccionado == "Alta") { filtroSeleccionado = "Alta" }
                FilterChipMatch("Media", filtroSeleccionado == "Media") { filtroSeleccionado = "Media" }
            }

            // LISTA FILTRADA
            val listaFiltrada = mockVacantesColaborador.filter { 
                (filtroSeleccionado == "Todos" || it.urgencia.contains(filtroSeleccionado, ignoreCase = true)) &&
                (searchQuery.isEmpty() || it.titulo.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(listaFiltrada) { vacante ->
                    VacanteMatchCard(vacante)
                }
            }
        }
    }
}

// ==========================================
// COMPONENTES UI (Privados para no conflictuar)
// ==========================================

@Composable
private fun VacanteMatchCard(vacante: VacanteColaboradorDto) {
    // Colores dinámicos según match
    val matchColor = when {
        vacante.match >= 80 -> Color(0xFF00C853) // Verde fuerte
        vacante.match >= 50 -> Color(0xFFFFAB00) // Ambar
        else -> Color(0xFFD50000) // Rojo
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vacante.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BusinessCenter, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vacante.especialidad,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vacante.ubicacion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                UrgenciaBadgeMatch(vacante.urgencia)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MATCH BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Match con tu perfil",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF455A64)
                )
                Text(
                    text = "${vacante.match}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = matchColor
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { vacante.match / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = matchColor,
                trackColor = Color(0xFFECEFF1),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SKILLS
            Text("Skills requeridos:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vacante.skills.take(3).forEach { (skill, nivel) ->
                    SkillBadgeMatch(skill, nivel)
                }
                if(vacante.skills.size > 3) {
                    Text("+${vacante.skills.size - 3}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DESCRIPCIÓN
            Text(
                text = vacante.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF546E7A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOTÓN POSTULAR
        Button(
            onClick = { /* TODO: Acción postular */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00549F))
        ) {
            Text("Postular a esta vacante", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun UrgenciaBadgeMatch(nivel: String) {
    val (bgColor, textColor) = when (nivel) {
        "Alta" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        "Media" -> Color(0xFFFFF8E1) to Color(0xFFFFA000)
        else -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(
                text = nivel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SkillBadgeMatch(nombre: String, nivel: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(10.dp), tint = Color(0xFF00549F))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "$nombre ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(text = "($nivel)", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun FilterChipMatch(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFF00549F) else Color.White,
        contentColor = if (selected) Color.White else Color(0xFF00549F),
        border = if (!selected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null,
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(32.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}
