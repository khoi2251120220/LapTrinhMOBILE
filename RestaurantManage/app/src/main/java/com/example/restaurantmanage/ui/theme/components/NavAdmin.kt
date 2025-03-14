package com.example.restaurantmanage.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "dashboard") IconActiveColor else IconInactiveColor
                )
            },

        )
        NavigationBarItem(
            selected = currentRoute == "manage_bookings",
            onClick = {
                navController.navigate("manage_bookings") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_booking),
                    contentDescription = "Manage Bookings",
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "manage_bookings") IconActiveColor else IconInactiveColor
                )
            },

        )
        NavigationBarItem(
            selected = currentRoute == "reports",
            onClick = {
                navController.navigate("reports") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sale),
                    contentDescription = "Reports",
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "reports") IconActiveColor else IconInactiveColor
                )
            },

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
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "settings") IconActiveColor else IconInactiveColor
                )
            },

        )
    }
}