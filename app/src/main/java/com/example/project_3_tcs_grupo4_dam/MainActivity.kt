package com.example.project_3_tcs_grupo4_dam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavigation

/**
 * MainActivity - Punto de entrada de la aplicación
 *
 * INSTRUCCIONES DE USO:
 * 1. Asegúrate de que tu backend .NET esté corriendo en http://localhost:5260
 * 2. Si usas emulador, la URL http://10.0.2.2:5260 ya está configurada
 * 3. Si usas dispositivo físico, cambia la URL en RetrofitClient.kt a tu IP local
 *
 * El flujo es:
 * - Si NO hay sesión activa -> LoginScreen
 * - Si hay sesión activa -> Home según el rol (ADMIN, BUSINESS_MANAGER, COLABORADOR)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}

