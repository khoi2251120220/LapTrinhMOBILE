package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreenAdmin() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("menu_management") {
            MenuManagementScreen(navController)
        }
        composable("table_management") {
            TableManagementScreen(navController)
        }
        composable("settings") {
            // Tạm thời hiển thị màn hình trống cho Settings
            DashboardScreen(navController)
        }
    }
} 