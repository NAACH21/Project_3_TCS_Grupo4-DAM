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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.presentation.components.BottomNavBar

// ======================================================
// PANTALLA PARA ADMINISTRADOR
// ======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantScreen(navController: NavController, vacantViewModel: VacantViewModel = viewModel()) {

    val vacantes by vacantViewModel.vacantes.collectAsState()
    val isLoading by vacantViewModel.isLoading.collectAsState()
    val errorMessage by vacantViewModel.errorMessage.collectAsState()

    var searchText by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Recargar lista al volver a la pantalla
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vacantViewModel.fetchVacantes()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        bottomBar = { BottomNavBar(navController = navController) },
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

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage ?: "Error desconocido", color = Color.Red)
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(
                            vacantes.filter {
                                it.nombrePerfil.contains(searchText, ignoreCase = true)
                            }
                        ) { vacante ->
                            VacanteAdminCard(vacante)
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
// CARD COMPONENT PARA ADMIN
// ======================================================
@Composable
fun VacanteAdminCard(vacante: VacanteResponse) {

    // Normalizar valores que pueden venir null
    val urgencia = vacante.urgencia ?: "-"
    val urgenciaUpper = urgencia.uppercase()

    val estado = vacante.estadoVacante ?: "-"
    val estadoUpper = estado.uppercase()

    val area = vacante.area ?: "-"
    val rol = vacante.rolLaboral ?: "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Nombre del perfil
                Text(
                    text = vacante.nombrePerfil,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Badge de urgencia
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (urgenciaUpper) {
                        "ALTA" -> Color(0xFFFFEBEE)
                        "MEDIA" -> Color(0xFFFFF3E0)
                        "BAJA" -> Color(0xFFE8F5E9)
                        else -> Color.Gray
                    }
                ) {
                    Text(
                        text = urgencia,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = when (urgenciaUpper) {
                            "ALTA" -> Color(0xFFC62828)
                            "MEDIA" -> Color(0xFFEF6C00)
                            "BAJA" -> Color(0xFF2E7D32)
                            else -> Color.White
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Área • Rol
            Text(
                text = "$area • $rol",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estado de la vacante
            Row {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (estadoUpper) {
                        "ABIERTA" -> Color(0xFF1976D2)
                        "OCUPADA" -> Color(0xFF388E3C)
                        "CERRADA" -> Color(0xFF757575)
                        else -> Color.Gray
                    }
                ) {
                    Text(
                        text = estado,
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
