package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos.LoginRequest
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val repo: AuthRepository): ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState(error = "Email y contraseña no pueden estar vacíos")
            return
        }

        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            when (val result = repo.login(loginRequest)) {
                is Result.Success -> {
                    if (result.data.success) {
                        _state.value = AuthUiState(success = true)
                    } else {
                        _state.value = AuthUiState(error = result.data.message)
                    }
                }
                is Result.Error -> {
                    _state.value = AuthUiState(error = result.exception.message ?: "Error desconocido")
                }
            }
        }
    }

    fun register(nombreCompleto: String, email: String, password: String) {
        if (nombreCompleto.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState(error = "Todos los campos son obligatorios")
            return
        }

        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val registerRequest = RegisterRequest(nombreCompleto, email, password)
            when (val result = repo.register(registerRequest)) {
                is Result.Success -> {
                    if (result.data.success) {
                        _state.value = AuthUiState(success = true)
                    } else {
                        _state.value = AuthUiState(error = result.data.message)
                    }
                }
                is Result.Error -> {
                    _state.value = AuthUiState(error = result.exception.message ?: "Error desconocido")
                }
            }
        }
    }

    fun token(): String? = repo.token()

    fun resetState() {
        _state.value = AuthUiState()
    }
}
