package com.example.restaurantmanage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.restaurantmanage.ui.theme.screens.MainScreen
//import com.example.restaurantmanage.ui.theme.screens.LoginScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
//        composable("login") { LoginScreen(navController) }
        composable("user") { MainScreen(navController) }
        composable("admin") { MainScreen(navController) }

    }
}