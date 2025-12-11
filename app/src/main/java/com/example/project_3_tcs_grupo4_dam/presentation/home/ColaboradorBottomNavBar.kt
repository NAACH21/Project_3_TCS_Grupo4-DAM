package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.Routes

// Clase de datos local para los items, ya que es específica para esta barra de navegación
private data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun ColaboradorBottomNavBar(
    navController: NavController,
    alertCount: Int // Se elimina el valor por defecto para solucionar el conflicto
) {
    val items = listOf(
        BottomNavItem("Inicio", Routes.COLABORADOR_HOME, Icons.Default.Home),
        BottomNavItem("Vacantes", Routes.VACANTES_COLABORADOR, Icons.Default.Work),
        BottomNavItem("Mis Solicitudes", Routes.SOLICITUDES_COLABORADOR, Icons.Default.Assignment),
        BottomNavItem("Notificaciones", Routes.ALERTAS_COLABORADOR, Icons.Default.Notifications, alertCount)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF00549F)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(badge = { Badge { Text(text = item.badgeCount.toString()) } }) {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00549F),
                    selectedTextColor = Color(0xFF00549F),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF00549F).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColaboradorBottomNavBarPreview() {
    val navController = rememberNavController()
    // Previsualizamos con 3 notificaciones para ver el badge
    ColaboradorBottomNavBar(navController = navController, alertCount = 3)
}
