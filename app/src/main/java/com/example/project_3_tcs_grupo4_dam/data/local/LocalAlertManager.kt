package com.example.project_3_tcs_grupo4_dam.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestiona localmente los IDs de las notificaciones que ya han sido leídas.
 * Esto permite persistencia simple sin tocar el backend.
 */
class LocalAlertManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("alerts_prefs", Context.MODE_PRIVATE)
    private val KEY_READ_IDS = "read_alert_ids"

    // Obtener todos los IDs vistos
    fun getReadIds(): Set<String> {
        return prefs.getStringSet(KEY_READ_IDS, emptySet()) ?: emptySet()
    }

    // Marcar un ID como visto
    fun markAsRead(id: String) {
        val currentSet = getReadIds().toMutableSet()
        if (currentSet.add(id)) {
            prefs.edit().putStringSet(KEY_READ_IDS, currentSet).apply()
        }
    }
}
