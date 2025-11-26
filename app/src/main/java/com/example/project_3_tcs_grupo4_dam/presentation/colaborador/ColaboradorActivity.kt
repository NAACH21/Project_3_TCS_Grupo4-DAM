package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.example.project_3_tcs_grupo4_dam.presentation.ui.theme.Project_3_TCS_Grupo4DAMTheme

class ColaboradorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Project_3_TCS_Grupo4DAMTheme {
                ColaboradorPlaceholder()
            }
        }
    }
}

@Composable
fun ColaboradorPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "ColaboradorActivity (placeholder) - refactorizar a Composable en presentation.colaborador")
    }
}
