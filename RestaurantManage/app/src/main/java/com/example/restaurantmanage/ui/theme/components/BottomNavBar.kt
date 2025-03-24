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
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth()
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
                    contentDescription = "Home",
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "home") IconActiveColor else IconInactiveColor
                )
            }
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
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "booking") IconActiveColor else IconInactiveColor
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == "payment",
            onClick = {
                navController.navigate("payment") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sale),
                    contentDescription = "Payment",
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "payment") IconActiveColor else IconInactiveColor
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == "favorites",
            onClick = {
                navController.navigate("favorites") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = "Favorites",
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "favorites") IconActiveColor else IconInactiveColor
                )
            }
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
                    modifier = Modifier.size(30.dp),
                    tint = if (currentRoute == "profile") IconActiveColor else IconInactiveColor
                )
            }
        )
    }
}