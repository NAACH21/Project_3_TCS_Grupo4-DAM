package com.example.project_3_tcs_grupo4_dam.presentation.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Campo de texto con selector de fecha (DatePicker) de Material 3
 *
 * @param label Etiqueta del campo
 * @param selectedDate Fecha seleccionada (LocalDate o null)
 * @param onDateSelected Callback cuando se selecciona una fecha
 * @param modifier Modificador
 * @param isRequired Si el campo es obligatorio (muestra asterisco)
 * @param isError Si hay error de validación
 * @param errorMessage Mensaje de error a mostrar
 * @param enabled Si el campo está habilitado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Formatear fecha para mostrar en el campo
    val displayText = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

    OutlinedTextField(
        value = displayText,
        onValueChange = { /* No hacer nada, es solo lectura */ },
        label = {
            Text(if (isRequired) "$label *" else label)
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                if (enabled) showDatePicker = true
            },
        readOnly = true,
        enabled = enabled,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        trailingIcon = {
            IconButton(
                onClick = { if (enabled) showDatePicker = true },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = if (enabled) Color(0xFF00549F) else Color.Gray
                )
            }
        },
        singleLine = true
    )

    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = Color(0xFF00549F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF00549F),
                    todayContentColor = Color(0xFF00549F),
                    todayDateBorderColor = Color(0xFF00549F)
                )
            )
        }
    }
}

/**
 * Convierte LocalDate a formato ISO (yyyy-MM-dd) para enviar al backend
 */
fun LocalDate.toIsoString(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE)

/**
 * Convierte string ISO (yyyy-MM-dd) a LocalDate
 */
fun String.toLocalDateOrNull(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        null
    }
}

