package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar

// ======================================================
// DTOs & MOCK DATA
// ======================================================

data class VacanteDto(
    val id: Int,
    val titulo: String,
    val especialidad: String,
    val ubicacion: String,
    val urgencia: String, // "Alta", "Media", "Baja"
    val match: Int,
    val skills: List<Pair<String, String>>, // skill - nivel
    val descripcion: String
)

val mockVacantes = listOf(
    VacanteDto(
        id = 1,
        titulo = "Senior React Developer",
        especialidad = "Desarrollo Frontend",
        ubicacion = "Lima, Perú",
        urgencia = "Alta",
        match = 98,
        skills = listOf(
            "React" to "Avanzado",
            "TypeScript" to "Avanzado",
            "CSS/SASS" to "Intermedio",
            "Git" to "Intermedio"
        ),
        descripcion = "Buscamos un desarrollador React senior para liderar proyectos de banca digital y mentorear al equipo junior."
    ),
    VacanteDto(
        id = 2,
        titulo = "Backend Developer Node.js",
        especialidad = "Desarrollo Backend",
        ubicacion = "Remoto",
        urgencia = "Alta",
        match = 85,
        skills = listOf(
            "Node.js" to "Avanzado",
            "Express" to "Avanzado",
            "MongoDB" to "Intermedio",
            "Docker" to "Básico"
        ),
        descripcion = "Únete a nuestro equipo de microservicios para construir APIs escalables y de alto rendimiento."
    ),
    VacanteDto(
        id = 3,
        titulo = "DevOps Engineer",
        especialidad = "Infraestructura",
        ubicacion = "Híbrido - Lima",
        urgencia = "Media",
        match = 65,
        skills = listOf(
            "AWS" to "Avanzado",
            "Kubernetes" to "Intermedio",
            "CI/CD" to "Avanzado",
            "Python" to "Básico"
        ),
        descripcion = "Ingeniero DevOps para automatizar pipelines y gestionar infraestructura cloud en AWS."
    ),
    VacanteDto(
        id = 4,
        titulo = "Full Stack Developer",
        especialidad = "Desarrollo Web",
        ubicacion = "Lima, Perú",
        urgencia = "Baja",
        match = 40,
        skills = listOf(
            "Java" to "Intermedio",
            "Spring Boot" to "Intermedio",
            "Angular" to "Básico"
        ),
        descripcion = "Desarrollador Full Stack para mantenimiento de aplicaciones legacy y migración a nuevas tecnologías."
    )
)

// Colores personalizados basados en la imagen
private val TCSGreen = Color(0xFF00A651) // Color aproximado botón
private val UrgencyHighColor = Color(0xFFFFCDD2) // Fondo rojo claro
private val UrgencyHighText = Color(0xFFD32F2F) // Texto rojo
private val UrgencyMedColor = Color(0xFFFFF9C4) // Fondo amarillo claro
private val UrgencyMedText = Color(0xFFFBC02D) // Texto amarillo oscuro
private val UrgencyLowColor = Color(0xFFE8F5E9) // Fondo verde claro
private val UrgencyLowText = Color(0xFF388E3C) // Texto verde

// ======================================================
// MAIN SCREEN
// ======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantesScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Filtramos la lista localmente
    val filteredList = mockVacantes.filter { vacante ->
        val matchesSearch = vacante.titulo.contains(searchQuery, ignoreCase = true) ||
                            vacante.especialidad.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Urgencia Alta" -> vacante.urgencia == "Alta"
            "Urgencia Media" -> vacante.urgencia == "Media"
            "Urgencia Baja" -> vacante.urgencia == "Baja"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacantes para Ti", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Icono simulado de back o menú si se desea
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            // Usamos el BottomNavBar de colaborador que ya existe en tu proyecto
            BottomNavBar(navController = navController)
        },
        containerColor = Color(0xFFFAFAFA) // Fondo gris muy claro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Barra de Búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Buscar vacantes...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Filtros (Chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipSimple(
                    label = "Todos",
                    selected = selectedFilter == "Todos",
                    onClick = { selectedFilter = "Todos" }
                )
                FilterChipSimple(
                    label = "Urgencia Alta",
                    selected = selectedFilter == "Urgencia Alta",
                    onClick = { selectedFilter = "Urgencia Alta" }
                )
                FilterChipSimple(
                    label = "Urgencia Media",
                    selected = selectedFilter == "Urgencia Media",
                    onClick = { selectedFilter = "Urgencia Media" }
                )
                // Puedes agregar más si caben o usar LazyRow
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${filteredList.size} vacantes disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Lista de Vacantes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredList) { vacante ->
                    VacanteCard(vacante = vacante)
                }
            }
        }
    }
}

// ======================================================
// COMPONENTES UI
// ======================================================

@Composable
fun FilterChipSimple(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFF0D47A1) else Color.White,
        border = if (!selected) BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier.height(32.dp),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun VacanteCard(vacante: VacanteDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        ) {
            // Header: Título y Urgencia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vacante.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${vacante.especialidad} • ${vacante.ubicacion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                // Badge de Urgencia
                UrgenciaBadge(nivel = vacante.urgencia)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Match
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Match", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                }
                Text("${vacante.match}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (vacante.match > 75) TCSGreen else Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Barra de progreso lineal fina
            LinearProgressIndicator(
                progress = { vacante.match / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (vacante.match > 75) TCSGreen else if (vacante.match > 50) Color(0xFFFFB300) else Color(0xFFE53935),
                trackColor = Color(0xFFEEEEEE),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skills Requeridos
            Text("Skills Requeridos:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Usamos un FlowRow simple (o Column/Row combinado si no hay FlowRow en la versión de compose)
            // Para simplificar sin añadir dependencias experimentales, mostramos hasta 4 en lista vertical compacta o 2 filas
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                vacante.skills.chunked(2).forEach { rowSkills ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSkills.forEach { (skill, nivel) ->
                            SkillChipItem(skill, nivel)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción corta
            Text(
                text = vacante.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón full width al final
        Button(
            onClick = { /* Acción postular */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(0.dp), // Recto arriba si se quiere, o levemente redondeado abajo
            colors = ButtonDefaults.buttonColors(containerColor = TCSGreen)
        ) {
            Text("Postular a esta vacante", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun UrgenciaBadge(nivel: String) {
    val (bgColor, textColor) = when (nivel) {
        "Alta" -> UrgencyHighColor to UrgencyHighText
        "Media" -> UrgencyMedColor to UrgencyMedText
        "Baja" -> UrgencyLowColor to UrgencyLowText
        else -> Color.LightGray to Color.Black
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = nivel.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp
        )
    }
}

@Composable
fun SkillChipItem(skill: String, nivel: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = TCSGreen
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$skill: ",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = nivel,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
