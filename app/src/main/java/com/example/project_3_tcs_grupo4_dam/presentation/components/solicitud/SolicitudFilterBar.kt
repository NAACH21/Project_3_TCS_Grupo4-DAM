package com.example.project_3_tcs_grupo4_dam.presentation.components.solicitud

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val TCSBlue = Color(0xFF00549F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFilterBar(
    selectedTipo: String,
    onTipoChange: (String) -> Unit,
    selectedEstado: String,
    onEstadoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Filtro por Tipo - SOLO Certificación y Actualización de Skills para colaborador
        Text(
            text = "Tipo de solicitud",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tipos = listOf(
                "TODOS" to "Todas",
                "CERTIFICACION" to "Certificación",
                "ACTUALIZACION_SKILLS" to "Actualización Skills"
                // NO incluir "ENTREVISTA_DESEMPENO" - es solo para admin
            )

            tipos.forEach { (value, label) ->
                FilterChip(
                    selected = selectedTipo == value,
                    onClick = { onTipoChange(value) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TCSBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro por Estado
        Text(
            text = "Estado",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val estados = listOf(
                "TODOS" to "Todos",
                "PENDIENTE" to "Pendiente",
                "EN_REVISION" to "En Revisión",
                "APROBADA" to "Aprobada",
                "RECHAZADA" to "Rechazada",
                "PROGRAMADA" to "Programada"
            )

            estados.forEach { (value, label) ->
                FilterChip(
                    selected = selectedEstado == value,
                    onClick = { onEstadoChange(value) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TCSBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}
