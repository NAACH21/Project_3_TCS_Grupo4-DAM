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

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Routes.HOME, "Inicio", Icons.Default.Home)
    object Colaboradores : BottomNavItem(Routes.COLABORADORES, "Colaboradores", Icons.Default.Person)
    // Corrected Route to point to the main evaluation screen
    object Evaluaciones : BottomNavItem(Routes.EVALUATION_SCREEN, "Evaluaciones", Icons.Default.Assessment)
    object Vacantes : BottomNavItem(Routes.VACANTES, "Vacantes", Icons.Default.Work)
    object Dashboard : BottomNavItem(Routes.DASHBOARD, "Dashboard", Icons.Default.Dashboard)
}

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Colaboradores,
        BottomNavItem.Evaluaciones,
        BottomNavItem.Vacantes,
        BottomNavItem.Dashboard
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF0A63C2)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Evitar acumular pantallas en el back stack
                            popUpTo(Routes.HOME) {
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
