package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.restaurantmanage.navigation.AdminNavGraph
import com.example.restaurantmanage.navigation.UserNavGraph
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(navController: NavHostController) {
    // Kiểm tra route hiện tại để điều hướng đến NavGraph con
    when (navController.currentBackStackEntry?.destination?.route) {
        "user" -> UserNavGraph(navController)
        "admin" -> AdminNavGraph(navController)
        else -> {
            // Giao diện mặc định của MainScreen khi không phải user hoặc admin
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tiêu đề
                Text(
                    text = "Màn Hình Chính",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Nút điều hướng đến RatingScreen
                Button(
                    onClick = {
                        navController.navigate("rating") // Điều hướng đến RatingScreen
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Đi đến màn hình đánh giá", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RestaurantManageTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}