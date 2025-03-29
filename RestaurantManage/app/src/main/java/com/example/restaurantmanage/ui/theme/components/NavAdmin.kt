package com.example.restaurantmanage.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.ui.theme.IconActiveColor
import com.example.restaurantmanage.ui.theme.IconInactiveColor

@Composable
fun NavAdmin(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Draw only the top border
                val strokeWidth = 1f
                val y = 0f
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        containerColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = {
                navController.navigate("dashboard") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "dashboard") IconActiveColor else IconInactiveColor
                )
            },
            label = {
                Text(
                    text = "Dashboard",
                    color = if (currentRoute == "dashboard") IconActiveColor else IconInactiveColor
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == "menu_management",
            onClick = {
                navController.navigate("menu_management") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_booking),
                    contentDescription = "Menu Management",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "menu_management") IconActiveColor else IconInactiveColor
                )
            },
            label = {
                Text(
                    text = "Menu",
                    color = if (currentRoute == "menu_management") IconActiveColor else IconInactiveColor
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == "table_management",
            onClick = {
                navController.navigate("table_management") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sale),
                    contentDescription = "Table Management",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "table_management") IconActiveColor else IconInactiveColor
                )
            },
            label = {
                Text(
                    text = "Tables",
                    color = if (currentRoute == "table_management") IconActiveColor else IconInactiveColor
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "settings") IconActiveColor else IconInactiveColor
                )
            },
            label = {
                Text(
                    text = "Settings",
                    color = if (currentRoute == "settings") IconActiveColor else IconInactiveColor
                )
            }
        )
    }
}