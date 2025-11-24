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

    fun getToken(): String? = _onToken.value
    fun getColaboradorId(): String? = _onColaboradorId.value


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

                    Log.d("LOGIN_BACKEND", "JSON recibido: $apiResponse")
                    Log.d("LOGIN_BACKEND", "Rol recibido: ${apiResponse.data?.rolSistema}")

                    if (apiResponse.success && apiResponse.data != null) {

                        // ⭐ Guardar token y colaboradorId ⭐
                        val token = apiResponse.data.token
                        _onToken.value = token
                        _onColaboradorId.value = apiResponse.data.colaboradorId
                        
                        // CONFIGURAR TOKEN EN RETROFIT PARA PETICIONES FUTURAS
                        RetrofitClient.setJwtToken(token)
                        Log.d("AUTH_VM", "Token JWT configurado en RetrofitClient")

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
