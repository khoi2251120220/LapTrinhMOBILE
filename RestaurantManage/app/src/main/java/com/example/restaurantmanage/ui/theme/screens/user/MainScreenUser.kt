package com.example.restaurantmanage.ui.theme.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.models.*
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.BottomNavBar
import com.example.restaurantmanage.ui.theme.screens.assignment.LoginScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.PasswordScreen
import com.example.restaurantmanage.ui.theme.screens.user.booking.BookingScreen
import com.example.restaurantmanage.ui.theme.screens.user.home.HomeScreen
import com.example.restaurantmanage.ui.theme.screens.user.introduce.IntroduceScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.FoodDetailScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.MenuScreen
import com.example.restaurantmanage.ui.theme.screens.user.personal.ProfileScreen
import com.example.restaurantmanage.viewmodels.MenuViewModel

@Composable
fun MainScreenUser() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: MenuViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (currentRoute != "introduce") {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "introduce",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("introduce") {
                IntroduceScreen(navController)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("booking") {
                BookingScreen(navController)
            }
            composable("menu") {
                MenuScreen(navController, sampleCategories)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("login_screen") {
                LoginScreen( navController)
            }
            composable("password_screen/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                PasswordScreen(navController = navController, email = email)
            }
            composable("detail/{menuItemId}") { backStackEntry ->
                val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: ""
                FoodDetailScreen(menuItemId, viewModel, navController)
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