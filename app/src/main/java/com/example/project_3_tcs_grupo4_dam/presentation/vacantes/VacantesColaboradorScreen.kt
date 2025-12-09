package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.Vacante // Usamos el modelo existente
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.presentation.home.ColaboradorBottomNavBar
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesViewModel

// Colores definidos según diseño
private val TCSBlue = Color(0xFF00549F)
private val LightGrayBg = Color(0xFFF5F7FA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)
private val ChipRemoteBg = Color(0xFFE3F2FD)
private val ChipRemoteText = Color(0xFF1565C0)
private val ChipOnSiteBg = Color(0xFFFBE9E7)
private val ChipOnSiteText = Color(0xFFD84315)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacantesColaboradorScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // ViewModel de vacantes
    val viewModel: VacantViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VacantViewModel() as T // VacantViewModel ya instancia el repo internamente en tu código
            }
        }
    )

    // ViewModel de notificaciones (para el badge del menú inferior)
    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )
    val unreadCount by notificacionesViewModel.unreadCount.collectAsState()

    val vacantes by viewModel.vacantes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    // El ViewModel actual usa errorMessage en lugar de error
    val error by viewModel.errorMessage.collectAsState()

    // Estado para el buscador
    var searchQuery by remember { mutableStateOf("") }

    // Filtrar vacantes localmente
    val vacantesFiltradas = remember(vacantes, searchQuery) {
        if (searchQuery.isBlank()) vacantes
        else vacantes.filter {
            it.nombrePerfil.contains(searchQuery, ignoreCase = true) ||
            it.area.contains(searchQuery, ignoreCase = true) ||
            it.rolLaboral.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchVacantes()
        notificacionesViewModel.cargarAlertas() // Asegurar que el badge esté actualizado
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Oportunidades Laborales", 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TCSBlue
                )
            )
        },
        bottomBar = { ColaboradorBottomNavBar(navController, unreadCount) },
        containerColor = LightGrayBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- Buscador ---
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            // --- Contenido ---
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = TCSBlue,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (error != null) {
                    ErrorState(
                        message = error ?: "Error desconocido",
                        onRetry = { viewModel.fetchVacantes() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (vacantesFiltradas.isEmpty()) {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(vacantesFiltradas) { vacante ->
                            VacanteCardModern(vacante)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        color = CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar por cargo, área...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TCSBlue) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TCSBlue,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = TCSBlue
            )
        )
    }
}

@Composable
fun VacanteCardModern(vacante: Vacante) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 1. Encabezado: Título y Modalidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vacante.nombrePerfil, // Usamos nombrePerfil como título
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (vacante.area.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Business, 
                                contentDescription = null, 
                                tint = TextSecondary, 
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Área: ${vacante.area}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                // Chip de Urgencia (Usamos urgencia como "tag" ya que no hay modalidad)
                val isHighPriority = vacante.urgencia.equals("Alta", ignoreCase = true)
                Surface(
                    color = if (isHighPriority) ChipRemoteBg else ChipOnSiteBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = vacante.urgencia.ifBlank { "Normal" },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isHighPriority) ChipRemoteText else ChipOnSiteText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Detalles (Ubicación/Rol, Fecha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Columna Izquierda
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (vacante.rolLaboral.isNotBlank()) {
                        DetailRow(icon = Icons.Default.Work, text = vacante.rolLaboral)
                    }
                    
                    val fechaTexto = try {
                        vacante.fechaInicio.take(10) // Usamos fechaInicio como referencia
                    } catch (e: Exception) { "Sin fecha" }
                    
                    DetailRow(icon = Icons.Default.CalendarToday, text = "Inicio: $fechaTexto")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Lista breve de skills requeridos
            if (vacante.skillsRequeridos.isNotEmpty()) {
                Text(
                    text = "Skills: " + vacante.skillsRequeridos.take(3).joinToString(", ") { it.nombre },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Botón de Acción
            Button(
                onClick = { /* TODO: Navegar al detalle */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TCSBlue
                )
            ) {
                Text(
                    text = "Ver Detalles y Postular",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Work,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay vacantes disponibles",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Intenta con otros filtros o vuelve más tarde.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Red
        )
        Text(text = message, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = TCSBlue)) {
            Text("Reintentar")
        }
    }
}
