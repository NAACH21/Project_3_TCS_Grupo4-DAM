package com.example.project_3_tcs_grupo4_dam.ui.colaborador.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.*

data class BottomBarItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomBarItem("Inicio", Routes.Home, Icons.Default.Home),
        BottomBarItem("Skills", Routes.Skills, Icons.Default.Star),
        BottomBarItem("Vacantes", Routes.Vacantes, Icons.Default.AccountBox),
        BottomBarItem("Notif.", Routes.Notificaciones, Icons.Default.Notifications)
    )

    NavigationBar {
        val backStackEntry = navController.currentBackStackEntryAsState()

        items.forEach { item ->
            NavigationBarItem(
                selected = backStackEntry.value?.destination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}
