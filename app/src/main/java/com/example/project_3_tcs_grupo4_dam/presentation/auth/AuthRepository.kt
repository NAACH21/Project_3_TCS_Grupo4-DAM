package com.example.project_3_tcs_grupo4_dam.presentation.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.project_3_tcs_grupo4_dam.BuildConfig

/**
 * Interfaz para autenticación básica.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    fun token(): String?
}

/**
 * Implementación simple que simula una llamada a backend.
 * Reemplaza con Retrofit cuando tengas tu API lista.
 */
class AuthRepositoryImpl(
    private val backendUrl: String = BuildConfig.BACKEND_URL
): AuthRepository {

    // Token en memoria (puedes migrar a DataStore / EncryptedSharedPreferences)
    @Volatile
    private var inMemoryToken: String? = null

    override suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        // Simulación de latencia
        delay(600)
        if (email.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email y contraseña requeridos"))
        }
        // Validación mínima simulada
        return@withContext if (password.length >= 4) {
            inMemoryToken = "fake-token-${'$'}{System.currentTimeMillis()}"
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Credenciales inválidas"))
        }
    }

    override fun token(): String? = inMemoryToken
}

