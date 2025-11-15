package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BulkUploadUiState(
    val fileUri: Uri? = null,
    val fileName: String? = null
)

class BulkUploadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BulkUploadUiState())
    val uiState = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?, name: String?) {
        _uiState.update { it.copy(fileUri = uri, fileName = name) }
    }

    fun processFile() {
        // TODO: Implement file processing logic here
        // val content = context.contentResolver.openInputStream(uiState.value.fileUri)
    }

    fun downloadTemplate() {
        // TODO: Implement CSV template download logic
    }
}