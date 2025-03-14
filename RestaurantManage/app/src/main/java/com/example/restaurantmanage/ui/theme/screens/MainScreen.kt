package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.restaurantmanage.navigation.AdminNavGraph
import com.example.restaurantmanage.navigation.UserNavGraph

@Composable
fun MainScreen(navController: NavHostController) {
    when (navController.currentBackStackEntry?.destination?.route) {
        "user" -> UserNavGraph(navController)
        "admin" -> AdminNavGraph(navController)
//        else -> LoginScreen(navController)
    }
}