package com.example.restaurantmanage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.restaurantmanage.ui.theme.screens.*
import com.example.restaurantmanage.ui.theme.screens.admin.*
import com.example.restaurantmanage.ui.theme.screens.assignment.LoginScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.RegisterScreen

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object Register : Screen("register")

    // Public Screens (No login required)
    object Home : Screen("home")
    object Menu : Screen("menu")
    object RestaurantInfo : Screen("restaurant_info")
    object Contact : Screen("contact")


    // User Screens (Requires login)
    object UserProfile : Screen("user/profile")
    object UserOrders : Screen("user/orders")
    object UserBookings : Screen("user/bookings")
    object UserFavorites : Screen("user/favorites")
    object Cart : Screen("user/cart")

    // Admin Screens (Requires admin login)
    object AdminDashboard : Screen("admin/dashboard")
    object MenuManagement : Screen("admin/menu_management")
    object TableManagement : Screen("admin/table_management")
    object OrderManagement : Screen("admin/order_management")
    object UserManagement : Screen("admin/user_management")
    object Reports : Screen("admin/reports")
    object Settings : Screen("admin/settings")
}
// tạm thời comment lại để tránh lỗi sẽ xử lí sau khi thống nhất
//@Composable
//fun AppNavigation(
//    navController: NavHostController,
//    isAuthenticated: Boolean = false,
//    isAdmin: Boolean = false
//) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.Home.route
//    ) {
//        // Public Routes (No login required)
//        composable(Screen.Home.route) {
//            HomeScreen(navController)
//        }
//        composable(Screen.Menu.route) {
//            MenuScreen(navController)
//        }
//        composable(Screen.RestaurantInfo.route) {
//            RestaurantInfoScreen(navController)
//        }
//        composable(Screen.Contact.route) {
//            ContactScreen(navController)
//        }
//
//        // Auth Routes
//        composable(Screen.Login.route) {
//            LoginScreen(navController)
//        }
//        composable(Screen.Register.route) {
//            RegisterScreen(navController)
//        }
//
//        // User Routes (Requires login)
//        composable(Screen.UserProfile.route) {
//            if (isAuthenticated) {
//                UserProfileScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.UserOrders.route) {
//            if (isAuthenticated) {
//                UserOrdersScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.UserBookings.route) {
//            if (isAuthenticated) {
//                UserBookingsScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.UserFavorites.route) {
//            if (isAuthenticated) {
//                UserFavoritesScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.Cart.route) {
//            if (isAuthenticated) {
//                CartScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.Checkout.route) {
//            if (isAuthenticated) {
//                CheckoutScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//
//        // Admin Routes (Requires admin login)
//        composable(Screen.AdminDashboard.route) {
//            if (isAdmin) {
//                AdminDashboardScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.MenuManagement.route) {
//            if (isAdmin) {
//                MenuManagementScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.TableManagement.route) {
//            if (isAdmin) {
//                TableManagementScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.OrderManagement.route) {
//            if (isAdmin) {
//                OrderManagementScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.UserManagement.route) {
//            if (isAdmin) {
//                UserManagementScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.Reports.route) {
//            if (isAdmin) {
//                ReportsScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//        composable(Screen.Settings.route) {
//            if (isAdmin) {
//                SettingsScreen(navController)
//            } else {
//                navController.navigate(Screen.Login.route)
//            }
//        }
//    }
//}