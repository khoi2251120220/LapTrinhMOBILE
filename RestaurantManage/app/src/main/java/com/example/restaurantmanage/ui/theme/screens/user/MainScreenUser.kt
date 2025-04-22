package com.example.restaurantmanage.ui.theme.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.ui.theme.components.BottomNavBar
import com.example.restaurantmanage.ui.theme.screens.assignment.LoginScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.PasswordScreen
import com.example.restaurantmanage.ui.theme.screens.user.booking.BookingScreen
import com.example.restaurantmanage.ui.theme.screens.user.home.HomeScreen
import com.example.restaurantmanage.ui.theme.screens.user.introduce.IntroduceScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.CartScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.FoodDetailScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.MenuScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.PaymentSuccessScreen
import com.example.restaurantmanage.ui.theme.screens.user.order.RatingScreen
import com.example.restaurantmanage.ui.theme.screens.user.personal.ProfileScreen
import com.example.restaurantmanage.viewmodels.CartViewModel
import com.example.restaurantmanage.viewmodels.CartViewModelFactory
import com.example.restaurantmanage.viewmodels.MenuViewModel
import com.example.restaurantmanage.viewmodels.MenuViewModelFactory
import com.example.restaurantmanage.viewmodels.HomeViewModelFactory
import com.example.restaurantmanage.viewmodels.OrderViewModel
import com.example.restaurantmanage.viewmodels.OrderViewModelFactory
import com.example.restaurantmanage.viewmodels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreenUser() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val database = RestaurantDatabase.getDatabase(context)
    
    // Khởi tạo ViewModel với đầy đủ dependencies
    val menuViewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(
            menuItemDao = database.menuItemDao(),
            categoryDao = database.categoryDao()
        )
    )
    
    // Khởi tạo CartViewModel
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(database)
    )
    
    // Khởi tạo OrderViewModel
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(database)
    )
    
    // Khởi tạo ProfileViewModel
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(RestaurantDatabase.getDatabase(LocalContext.current))
    )

    Scaffold(
        bottomBar = {
            if (currentRoute != "introduce" && currentRoute != "payment_success" && 
                currentRoute != "cart" && currentRoute != "rating") {
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
                HomeScreen(
                    navController = navController,
                    homeViewModel = viewModel(
                        factory = HomeViewModelFactory(database)
                    )
                )
            }
            composable("booking") {
                BookingScreen(navController)
            }
            composable("menu") {
                MenuScreen(
                    navController = navController,
                    viewModel = menuViewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = profileViewModel,
                    orderViewModel = orderViewModel
                )
            }
            composable("login_screen") {
                LoginScreen(navController)
            }
            composable("password_screen/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                PasswordScreen(navController = navController, email = email)
            }
            composable("detail/{menuItemId}") { backStackEntry ->
                val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: ""
                FoodDetailScreen(menuItemId, menuViewModel, navController)
            }
            composable("cart") {
                CartScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    orderViewModel = orderViewModel,
                    profileViewModel = profileViewModel
                )
            }
            composable("payment_success/{orderId}/{customerName}/{amount}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                val customerName = backStackEntry.arguments?.getString("customerName") ?: "Khách hàng"
                val amount = backStackEntry.arguments?.getString("amount") ?: "0"
                
                val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                PaymentSuccessScreen(
                    navController = navController,
                    orderId = orderId,
                    customerName = customerName,
                    amount = "$amount VNĐ",
                    paymentTime = currentDate
                )
            }
            composable("rating") {
                RatingScreen(navController)
            }
        }
    }
}

