package com.example.project_3_tcs_grupo4_dam.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
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
    object Home : BottomNavItem(Routes.HomeScreen.route, "Inicio", Icons.Default.Home)
    object Colaboradores : BottomNavItem(Routes.ColaboradorListScreen.route, "Colaboradores", Icons.Default.Person)
    object Evaluaciones : BottomNavItem(Routes.EvaluationScreen.route, "Evaluaciones", Icons.Default.Assessment)
    object Vacantes : BottomNavItem(Routes.VacantesScreen.route, "Vacantes", Icons.Default.Work)
    object Dashboard : BottomNavItem(Routes.DashboardScreen.route, "Dashboard", Icons.Default.Dashboard)
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
                            popUpTo(Routes.HomeScreen.route) {
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