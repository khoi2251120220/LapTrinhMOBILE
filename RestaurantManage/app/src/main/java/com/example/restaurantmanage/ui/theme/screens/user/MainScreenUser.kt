package com.example.restaurantmanage.ui.theme.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.BottomNavBar
import com.example.restaurantmanage.ui.theme.screens.assignment.LoginScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.PasswordScreen
import com.example.restaurantmanage.ui.theme.screens.user.booking.BookingScreen
import com.example.restaurantmanage.ui.theme.screens.user.home.HomeScreen
import com.example.restaurantmanage.ui.theme.screens.user.MenuScreen
import com.example.restaurantmanage.ui.theme.screens.user.personal.ProfileScreen

@Composable
fun MainScreenUser() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("booking") {
                BookingScreen(navController)
            }
            composable("menu") {
                MenuScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            // Assignment routes
            composable("login_screen") {
                LoginScreen(navController = navController)
            }
            composable("password_screen/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                PasswordScreen(navController = navController, email = email)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RestaurantManageTheme {
        MainScreenUser()
    }
}