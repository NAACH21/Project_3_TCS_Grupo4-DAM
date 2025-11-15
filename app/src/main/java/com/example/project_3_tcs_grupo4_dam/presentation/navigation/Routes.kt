package com.example.project_3_tcs_grupo4_dam.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val EVALUATION_SCREEN = "evaluation"
    const val BULK_UPLOAD_SCREEN = "bulk_upload"
    const val EVALUATIONS_HISTORY_SCREEN = "evaluations_history"
    const val EVALUATION_DETAIL_SCREEN = "evaluation_detail/{id}" // Example with argument

    fun evaluationDetail(id: String) = "evaluation_detail/$id"
}
