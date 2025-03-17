package com.example.restaurantmanage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.restaurantmanage.ui.theme.screens.*


@Composable
fun UserNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dashboard") {


        composable("booking") { BookingScreen(navController) }
//        composable("payment") { PaymentScreen(navController) }
//        composable("favorites") { FavoritesScreen(navController) }
//        composable("profile") { ProfileScreen(navController) }

    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("cart") { CartScreen(navController) }
    }
}
