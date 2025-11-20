package com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto

@Composable
fun ColaboradorCard(
    colaborador: ColaboradorListDto, // Actualizado a ListDto
    modifier: Modifier = Modifier,
    onVerDetalle: () -> Unit = {},
    onEditar: (ColaboradorListDto) -> Unit = {},
    onEliminar: (ColaboradorListDto) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        color = MaterialTheme.colorScheme.onPrimary,
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
                        fontWeight = FontWeight.SemiBold
                    )
                    // Rol (puede ser nulo en ListDto)
                    Text(
                        text = colaborador.rol ?: "Sin rol",
                        style = MaterialTheme.typography.bodySmall
                    )
                    // Área (puede ser nulo)
                    Text(
                        text = colaborador.area ?: "Sin área",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Menú de opciones
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                showMenu = false
                                onEditar(colaborador)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                showMenu = false
                                onEliminar(colaborador)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de estado
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Estado general (si existe en el DTO)
                val estado = colaborador.estado ?: "Activo"
                AssistChip(
                    onClick = {},
                    label = { Text(estado) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (estado.equals("Disponible", ignoreCase = true)) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón "Ver detalle"
            OutlinedButton(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }
        }
    }
}
