package com.example.restaurantmanage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.ui.theme.screens.admin.MainScreenAdmin
import com.example.restaurantmanage.ui.theme.screens.assignment.LoginScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.PasswordScreen
import com.example.restaurantmanage.ui.theme.screens.assignment.RegisterScreen
import com.example.restaurantmanage.ui.theme.screens.user.MainScreenUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    val firestore = FirebaseFirestore.getInstance()
                    val database = RestaurantDatabase.getDatabase(this)
                    val menuItemDao = database.menuItemDao()

                    val startDestination = if (auth.currentUser != null) {
                        var destination = "user_screen"
                        auth.currentUser?.uid?.let { userId ->
                            firestore.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val role = document.getString("role") ?: "user"
                                    destination = if (role == "admin") "admin_screen" else "user_screen"
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                        }
                        destination
                    } else {
                        "login_screen"
                    }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login_screen") {
                            LoginScreen(navController)
                        }
                        composable(
                            "password_screen/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            PasswordScreen(navController, email)
                        }
                        composable("register_screen") {
                            RegisterScreen(navController)
                        }
                        composable("user_screen") {
                            MainScreenUser()
                        }
                        composable("admin_screen") {
                            MainScreenAdmin()
                        }
                    }
                }
            }
        }
    }
}