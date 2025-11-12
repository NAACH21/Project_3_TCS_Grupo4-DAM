package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val r = repo.login(email, password)
            _state.value = if (r.isSuccess) AuthUiState(success = true)
            else AuthUiState(error = r.exceptionOrNull()?.message)
        }
    }

    fun token(): String? = repo.token()
}
