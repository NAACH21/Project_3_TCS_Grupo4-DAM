package com.example.project_3_tcs_grupo4_dam.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Maneja la sesión del usuario usando EncryptedSharedPreferences
 * Guarda: token, rolSistema, colaboradorId, username, usuarioId
 */
class SessionManager(context: Context) {

    companion object {
        private const val FILE_NAME = "secure_session_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_ROL = "rol_sistema"
        private const val KEY_COLABORADOR_ID = "colaborador_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USUARIO_ID = "usuario_id" // Nuevo campo
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda la sesión del usuario después del login
     * Actualizado para incluir usuarioId
     */
    fun saveSession(token: String, rolSistema: String, colaboradorId: String?, username: String, usuarioId: String) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_ROL, rolSistema)
            putString(KEY_COLABORADOR_ID, colaboradorId)
            putString(KEY_USERNAME, username)
            putString(KEY_USUARIO_ID, usuarioId)
            apply()
        }
    }

    /**
     * Sobrecarga para compatibilidad temporal si se requiere,
     * aunque se recomienda usar la versión completa.
     */
    fun saveSession(token: String, rolSistema: String, colaboradorId: String?, username: String) {
        // Guarda una cadena vacía o nula para usuarioId si no se proporciona
        saveSession(token, rolSistema, colaboradorId, username, "")
    }

    /**
     * Obtiene el token de sesión
     */
    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    /**
     * Obtiene el rol del usuario (ADMIN, BUSINESS_MANAGER, COLABORADOR)
     */
    fun getRol(): String? = sharedPreferences.getString(KEY_ROL, null)

    /**
     * Obtiene el ID del colaborador
     */
    fun getColaboradorId(): String? = sharedPreferences.getString(KEY_COLABORADOR_ID, null)


    /**
     * Obtiene el username
     */
    fun getUsername(): String? = sharedPreferences.getString(KEY_USERNAME, null)

    /**
     * Obtiene el ID del usuario (Mongo ID)
     */
    fun getUsuarioId(): String? = sharedPreferences.getString(KEY_USUARIO_ID, null)

    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(): Boolean = getToken() != null

    /**
     * Cierra la sesión eliminando todos los datos
     */
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
