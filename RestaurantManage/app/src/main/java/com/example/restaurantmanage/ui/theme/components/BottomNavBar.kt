package com.example.restaurantmanage.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.ui.theme.IconActiveColor
import com.example.restaurantmanage.ui.theme.IconInactiveColor

@Composable
fun BottomNavBar(
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
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "home") IconActiveColor else IconInactiveColor
                )
            },
        )
        NavigationBarItem(
            selected = currentRoute == "booking",
            onClick = {
                navController.navigate("booking") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_booking),
                    contentDescription = "Booking",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "booking") IconActiveColor else IconInactiveColor
                )
            },
        )
        NavigationBarItem(
            selected = currentRoute == "menu",
            onClick = {
                navController.navigate("menu") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sale),
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "menu") IconActiveColor else IconInactiveColor
                )
            },

        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "profile") IconActiveColor else IconInactiveColor
                )
            },
        )
    }
}