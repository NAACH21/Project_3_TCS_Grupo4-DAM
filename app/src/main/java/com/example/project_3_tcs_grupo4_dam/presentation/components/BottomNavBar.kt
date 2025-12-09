package com.example.project_3_tcs_grupo4_dam.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// Clase de datos simple en lugar de sealed class con objetos estáticos
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    navController: NavController,
    // Parámetro opcional para definir la ruta home según el rol.
    homeRoute: String = Routes.ADMIN_HOME 
) {
    val items = listOf(
        BottomNavItem(homeRoute, "Inicio", Icons.Default.Home),
        BottomNavItem(Routes.COLABORADORES, "Colaboradores", Icons.Default.Person),
        BottomNavItem(Routes.EVALUACIONES_ADMIN, "Evaluaciones", Icons.Default.Assessment),
        BottomNavItem(Routes.VACANTES, "Vacantes", Icons.Default.Work), // Admin Vacantes
        BottomNavItem(Routes.DASHBOARD, "Dashboard", Icons.Default.Dashboard)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF0A63C2)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            
                            // Lógica especial para el botón "Inicio" para evitar que muestre pantallas guardadas (como notificaciones)
                            if (item.route == homeRoute) {
                                // Al ir a Inicio, limpiamos todo hasta el Inicio
                                popUpTo(homeRoute) {
                                    // NO guardamos el estado de lo que estamos cerrando (ej. Notificaciones)
                                    saveState = false 
                                    // Inclusive = false porque queremos quedarnos en homeRoute, no borrarlo
                                    inclusive = false
                                }
                                launchSingleTop = true
                                // NO restauramos estado para asegurar que se muestre el Home limpio
                                restoreState = false 
                            } else {
                                // Comportamiento estándar para las otras pestañas (Tabs)
                                popUpTo(homeRoute) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0A63C2),
                    selectedTextColor = Color(0xFF0A63C2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF0A63C2).copy(alpha = 0.1f)
                )
            )
        }
    }
}
