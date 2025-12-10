package com.example.project_3_tcs_grupo4_dam.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /**
     * Convierte una fecha desde un formato simple (ej. "dd/MM/yyyy") a ISO 8601 UTC.
     * @param dateString La fecha en formato "dd/MM/yyyy".
     * @return La fecha en formato ISO 8601 (ej. "2025-12-10T12:00:00.000Z") o null si el formato es inválido.
     */
    fun convertToIso8601(dateString: String): String? {
        if (dateString.isBlank()) return null

        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        return try {
            val date: Date = inputFormat.parse(dateString) ?: return null
            outputFormat.format(date)
        } catch (e: ParseException) {
            Log.e("DateUtils", "Error al parsear la fecha: $dateString", e)
            null
        }
    }
}
