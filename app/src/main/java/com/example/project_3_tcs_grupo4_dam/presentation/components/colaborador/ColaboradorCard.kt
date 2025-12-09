package com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto

// Color Corporativo TCS
private val PrimaryBlue = Color(0xFF00549F)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF666666)

@Composable
fun ColaboradorCard(
    colaborador: ColaboradorReadDto,
    modifier: Modifier = Modifier,
    onVerDetalle: () -> Unit = {},
    onEditar: (ColaboradorReadDto) -> Unit = {},
    onEliminar: (ColaboradorReadDto) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // fila superior: avatar + datos + menú
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar con iniciales
                val iniciales = (colaborador.nombres.firstOrNull()?.toString() ?: "") +
                        (colaborador.apellidos.firstOrNull()?.toString() ?: "")

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${colaborador.nombres} ${colaborador.apellidos}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = colaborador.rolLaboral,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = colaborador.area,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Menú de opciones
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = TextSecondary)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = Color.White
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar", color = TextPrimary) },
                            onClick = {
                                showMenu = false
                                onEditar(colaborador)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onEliminar(colaborador)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // chips de estado y disponibilidad para movilidad
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                
                // Chip Estado (Activo/Inactivo)
                val isActivo = colaborador.estado.equals("ACTIVO", ignoreCase = true)
                val bgEstado = if (isActivo) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Verde claro / Rojo claro
                val textEstado = if (isActivo) Color(0xFF2E7D32) else Color(0xFFC62828) // Verde oscuro / Rojo oscuro
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgEstado,
                    modifier = Modifier.height(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = colaborador.estado,
                            style = MaterialTheme.typography.labelSmall,
                            color = textEstado,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Chip Disponibilidad
                val isDisponible = colaborador.disponibleParaMovilidad
                val bgDisp = if (isDisponible) Color(0xFFE3F2FD) else Color(0xFFF5F5F5) // Azul claro / Gris claro
                val textDisp = if (isDisponible) PrimaryBlue else Color.Gray
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgDisp,
                    modifier = Modifier.height(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = if (isDisponible) "Disponible" else "No disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = textDisp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // botón "Ver detalle"
            OutlinedButton(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text("Ver detalle")
            }
        }
    }
}
