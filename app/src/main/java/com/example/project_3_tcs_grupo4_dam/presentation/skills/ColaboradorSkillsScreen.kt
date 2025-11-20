package com.example.project_3_tcs_grupo4_dam.presentation.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorSkillDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// Colores específicos
private val SkillApprovedGreen = Color(0xFFE8F5E9)
private val SkillApprovedText = Color(0xFF2E7D32)
private val SkillPendingOrange = Color(0xFFFFF3E0)
private val SkillPendingText = Color(0xFFEF6C00)
private val TCSBlue = Color(0xFF00549F)
private val ChipSelectedBg = Color(0xFF1E4F89)
private val ChipUnselectedBg = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorSkillsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val colaboradorId = sessionManager.getColaboradorId() ?: ""

    val viewModel: ColaboradorSkillsViewModel = viewModel(
        factory = ColaboradorSkillsViewModelFactory(
            RetrofitClient.colaboradorApi, 
            colaboradorId
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    // Estados locales para filtros
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Todos") }
    var selectedStatus by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Skills", fontWeight = FontWeight.Bold) },
                // CORRECCIÓN: Quitamos la flecha de retroceso
                navigationIcon = {}, 
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            ColaboradorBottomNavBar(navController)
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                placeholder = { Text("Buscar skills...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Filtros
            FilterSection(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Lista de Skills
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TCSBlue)
                }
            } else if (uiState.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage ?: "Error desconocido", color = Color.Red)
                }
            } else {
                val filteredSkills = viewModel.filterSkills(searchQuery, selectedType, selectedStatus)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredSkills) { skill ->
                        SkillColaboradorCard(
                            skill = skill,
                            onUpdateClick = {
                                // NAVEGACIÓN A ACTUALIZAR SKILL
                                navController.navigate(Routes.actualizarSkill(colaboradorId, skill.nombre))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(text = "Todos", isSelected = selectedType == "Todos", onClick = { onTypeSelected("Todos") })
            FilterChip(text = "Técnicos", isSelected = selectedType == "TECNICO", onClick = { onTypeSelected("TECNICO") })
            FilterChip(text = "Blandos", isSelected = selectedType == "BLANDO", onClick = { onTypeSelected("BLANDO") })
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(text = "Todos", isSelected = selectedStatus == "Todos", onClick = { onStatusSelected("Todos") })
            FilterChip(text = "Aprobados", isSelected = selectedStatus == "Aprobado", onClick = { onStatusSelected("Aprobado") })
            FilterChip(text = "Pendientes", isSelected = selectedStatus == "Pendiente", onClick = { onStatusSelected("Pendiente") })
            FilterChip(text = "Rechazados", isSelected = selectedStatus == "Rechazado", onClick = { onStatusSelected("Rechazado") })
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) ChipSelectedBg else ChipUnselectedBg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }.height(32.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SkillColaboradorCard(
    skill: ColaboradorSkillDto,
    onUpdateClick: () -> Unit
) {
    val estado = skill.estado ?: "PENDIENTE"
    val isApproved = estado.equals("APROBADO", ignoreCase = true)
    val statusColorBg = if (isApproved) SkillApprovedGreen else SkillPendingOrange
    val statusColorText = if (isApproved) SkillApprovedText else SkillPendingText

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isApproved) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SkillApprovedText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = skill.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(color = statusColorBg, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColorText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkillTag(text = skill.tipo, isLevel = false)
                SkillTag(text = getNivelLabel(skill.nivel), isLevel = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!skill.evidenciaUrl.isNullOrEmpty()) {
                Text(
                    text = "Evidencia: Ver evidencia",
                    style = MaterialTheme.typography.bodySmall,
                    color = TCSBlue,
                    modifier = Modifier.clickable { /* TODO */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onUpdateClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TCSBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar Skill", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SkillTag(text: String, isLevel: Boolean) {
    val colorText = if (isLevel) Color(0xFF2E7D32) else Color(0xFF666666)
    val colorBg = if (isLevel) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)

    Surface(
        color = colorBg,
        shape = RoundedCornerShape(6.dp),
        border = if (!isLevel) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = colorText
        )
    }
}
