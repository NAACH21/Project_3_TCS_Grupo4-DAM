package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepository
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * Estados de la UI para autenticación
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userRole: String? = null // Para navegación post-login
)

/**
 * ViewModel para el módulo de autenticación
 * Maneja la lógica de login y registro
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _onToken = MutableStateFlow<String?>(null)
    private val _onColaboradorId = MutableStateFlow<String?>(null)
    private val _onUsuarioId = MutableStateFlow<String?>(null)

    fun getToken(): String? = _onToken.value
    fun getColaboradorId(): String? = _onColaboradorId.value
    fun getUsuarioId(): String? = _onUsuarioId.value


    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Realiza el login del usuario
     */

    fun login(username: String, password: String) {
        // Validación básica
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(
                errorMessage = "Usuario y contraseña no pueden estar vacíos"
            )
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val request = AuthDtos.LoginRequest(username, password)

            repository.login(request).fold(
                onSuccess = { apiResponse ->

                    Log.d("AuthViewModel", "=== RESPUESTA JSON DEL BACKEND ===")
                    Log.d("AuthViewModel", "JSON completo: $apiResponse")
                    Log.d("AuthViewModel", "Success: ${apiResponse.success}")

                    if (apiResponse.success && apiResponse.data != null) {

                        // ⭐ El AuthRepository ya decodificó el JWT y guardó el usuarioId en SessionManager ⭐
                        val token = apiResponse.data.token
                        val colaboradorId = apiResponse.data.colaboradorId

                        // ⭐ Leer el usuarioId que el Repository extrajo del JWT y guardó ⭐
                        val usuarioId = repository.getSessionManager().getUsuarioId() ?: ""

                        _onToken.value = token
                        _onColaboradorId.value = colaboradorId
                        _onUsuarioId.value = usuarioId

                        Log.d("AuthViewModel", "=== VALORES CAPTURADOS ===")
                        Log.d("AuthViewModel", "UsuarioId (del JWT/SessionManager): $usuarioId")
                        Log.d("AuthViewModel", "ColaboradorId: $colaboradorId")
                        Log.d("AuthViewModel", "Token: ${token.take(20)}...")

                        // ⭐ VALIDACIÓN: Verificar que usuarioId NO esté vacío ⭐
                        if (usuarioId.isBlank()) {
                            Log.e("AuthViewModel", "❌ ERROR CRÍTICO: usuarioId está vacío después del login")
                            _uiState.value = AuthUiState(
                                errorMessage = "Error del servidor: No se pudo extraer el ID de usuario del token. Contacta al administrador."
                            )
                            return@fold
                        }

                        // CONFIGURAR TOKEN EN RETROFIT PARA PETICIONES FUTURAS
                        RetrofitClient.setJwtToken(token)
                        Log.d("AuthViewModel", "✅ Token JWT configurado en RetrofitClient")

                        _uiState.value = AuthUiState(
                            isSuccess = true,
                            userRole = apiResponse.data.rolSistema
                        )
                    } else {
                        _uiState.value = AuthUiState(
                            errorMessage = apiResponse.message
                        )
                    }
                }
                ,
                onFailure = { exception ->
                    // Error de red o excepción
                    Log.e("AuthViewModel", "Error en login", exception)
                    _uiState.value = AuthUiState(
                        errorMessage = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun register(nombreCompleto: String, email: String, password: String) {
        // Validación básica
        if (nombreCompleto.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(
                errorMessage = "Todos los campos son obligatorios"
            )
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val request = AuthDtos.RegisterRequest(nombreCompleto, email, password)

            repository.register(request).fold(
                onSuccess = { apiResponse ->
                    if (apiResponse.success) {
                        // Registro exitoso
                        _uiState.value = AuthUiState(
                            isSuccess = true,
                            errorMessage = "Registro exitoso. Ahora puedes iniciar sesión."
                        )
                    } else {
                        // Backend respondió pero con success=false
                        _uiState.value = AuthUiState(
                            errorMessage = apiResponse.message
                        )
                    }
                },
                onFailure = { exception ->
                    // Error de red o excepción
                    _uiState.value = AuthUiState(
                        errorMessage = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    /**
     * Verifica si hay sesión activa
     */
    fun checkSession(): Boolean = repository.isLoggedIn()

    /**
     * Obtiene el rol del usuario actual
     */
    fun getUserRole(): String? = repository.getRol()

    /**
     * Cierra sesión
     */
    fun logout() {
        repository.logout()
        RetrofitClient.clearToken() // Limpiar token de Retrofit también
        resetState()
    }

    /**
     * Resetea el estado de la UI
     */
    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
