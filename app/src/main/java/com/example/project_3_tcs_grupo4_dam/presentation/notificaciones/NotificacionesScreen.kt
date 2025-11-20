package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar

// Colores personalizados
private val BackgroundColor = Color(0xFFF7F4F2)
private val CardColor = Color(0xFFFDEFD8)
private val IconColor = Color(0xFFE67E22)
private val TextGray = Color(0xFF6D6D6D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Instanciamos el ViewModel
    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager) as T
            }
        }
    )

    val alertas by viewModel.alertas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundColor, 
        // IMPORTANTE: Usamos la misma BottomBar que el Home, pasando el contador
        bottomBar = { 
            ColaboradorBottomNavBar(navController, alertas.size) 
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Notificaciones", 
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = IconColor
                )
            } else if (alertas.isEmpty()) {
                // Estado vacío estilizado
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones nuevas",
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Lista de notificaciones
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 100.dp 
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alertas) { alerta ->
                        AlertaCard(alerta)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertaCard(alerta: AlertaDto) {
    // Parseo robusto del detalle para evitar mostrar JSON crudo
    // El backend devuelve 'detalle' como un Map (por GSON y Any?)
    val detalleMap = alerta.detalle as? Map<*, *>
    val descripcion = detalleMap?.get("descripcion")?.toString() ?: ""
    val skillsFaltantes = detalleMap?.get("skillsFaltantes") as? List<*>

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp), 
        shape = RoundedCornerShape(18.dp), 
        colors = CardDefaults.cardColors(
            containerColor = CardColor 
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp 
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Alerta",
                        tint = IconColor, 
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Tipo de alerta
                Text(
                    text = alerta.tipo.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Estado
                Text(
                    text = "Estado: ${alerta.estado.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )

                // Descripción principal
                if (descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray.copy(alpha = 0.9f)
                    )
                }

                // Lista de Skills Faltantes (si existen)
                if (!skillsFaltantes.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Skills faltantes:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    skillsFaltantes.forEach { skillObj ->
                        val skillMap = skillObj as? Map<*, *>
                        val nombre = skillMap?.get("nombre")?.toString() ?: "Skill"
                        val nivel = skillMap?.get("nivelRequerido")?.toString() ?: "?"
                        
                        Text(
                            text = "• $nombre (Nivel $nivel)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
