package com.example.project_3_tcs_grupo4_dam.presentation.components.colaborador

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColaboradoresHeader(
    modifier: Modifier = Modifier,
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onNuevoColaborador: () -> Unit = {},
    onCargaMasiva: () -> Unit = {},
    onFiltros: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            placeholder = { Text("Buscar por nombre o skill...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onFiltros,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Filtros")
            }

            OutlinedButton(
                onClick = onCargaMasiva,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Carga masiva")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNuevoColaborador,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+   Nuevo Colaborador")
        }
    }
}