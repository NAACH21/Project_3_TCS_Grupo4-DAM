package com.example.project_3_tcs_grupo4_dam.ui.colaborador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.project_3_tcs_grupo4_dam.ui.colaborador.navigation.NavGraph
import com.example.project_3_tcs_grupo4_dam.ui.theme.Project_3_TCS_Grupo4DAMTheme

class ColaboradorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Project_3_TCS_Grupo4DAMTheme {
                NavGraph()
            }
        }
    }
}
