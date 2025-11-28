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
    // Por defecto intenta inferir o usar ADMIN, pero lo pasaremos explícitamente.
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
                            // Al pulsar Home, limpiamos hasta el inicio del grafo
                            popUpTo(homeRoute) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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
