package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement.TableManagementScreen

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
        composable("user_management") {
            UserManagementScreen(navController)
        }
        composable("profile_admin") {
            ProfileAdminScreen(navController)
        }
    }
} 