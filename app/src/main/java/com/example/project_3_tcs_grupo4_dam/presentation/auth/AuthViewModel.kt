package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Exponemos el username como StateFlow para que la UI pueda observar cambios
    private val _username = MutableStateFlow<String?>(repository.getUsername())
    val username: StateFlow<String?> = _username.asStateFlow()

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
                    if (apiResponse.success && apiResponse.data != null) {
                        // Login exitoso: guardar estado y username
                        _uiState.value = AuthUiState(
                            isSuccess = true,
                            userRole = apiResponse.data.rolSistema
                        )
                        // Actualizar username reactivo
                        _username.value = apiResponse.data.username
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
     * Obtiene el username del usuario actual (desde el session manager / repositorio)
     */
    fun getUsername(): String? = repository.getUsername()

    /**
     * Cierra sesión
     */
    fun logout() {
        repository.logout()
        // Limpiar estado local
        _username.value = null
        resetState()
    }

    /**
     * Resetea el estado de la UI
     */
    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
