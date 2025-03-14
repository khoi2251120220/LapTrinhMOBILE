package com.example.restaurantmanage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.restaurantmanage.ui.theme.screens.*

@Composable
fun AdminNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
//        composable("manage_bookings") { ManageBookingsScreen(navController) }
//        composable("reports") { ReportsScreen(navController) }
//        composable("settings") { SettingsScreen(navController) }
    }
}