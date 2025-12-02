package com.example.project_3_tcs_grupo4_dam.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.dashboard.DashboardData
import com.example.project_3_tcs_grupo4_dam.data.repository.DashboardRepository
import kotlinx.coroutines.launch

// Estados de la UI para manejar carga y error
sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    private val _state = MutableLiveData<DashboardState>()
    val state: LiveData<DashboardState> = _state

    fun cargarMetricas() {
        _state.value = DashboardState.Loading

        viewModelScope.launch {
            val result = repository.obtenerMetricasAdmin()

            result.onSuccess { data ->
                _state.value = DashboardState.Success(data)
            }.onFailure { error ->
                _state.value = DashboardState.Error(error.message ?: "Error desconocido")
            }
        }
    }
}