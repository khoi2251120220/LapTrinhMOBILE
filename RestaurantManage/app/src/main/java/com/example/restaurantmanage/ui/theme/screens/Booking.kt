package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.restaurantmanage.data.viewmodels.BookingViewModel
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.ui.theme.components.BottomNavBar

@Composable
fun BookingScreen(navController: NavController) {
    val viewModel = BookingViewModel()
    val bookingData = viewModel.data.collectAsState().value
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            AppBar(
                title = "Đặt bàn",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Tìm bàn (phòng ăn)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
            items(bookingData ?: emptyList()) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Hình ảnh (thay bằng Coil/Glide)", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = booking.locationName, fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "★ ${booking.rating} (${booking.reviewCount} đánh giá)", fontSize = 14.sp)
                        }
                        Text(text = booking.price, fontSize = 16.sp, style = MaterialTheme.typography.headlineSmall)
                        Button(
                            onClick = { /* Xử lý chọn */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "Chọn")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    RestaurantManageTheme {
        BookingScreen(navController = NavController(androidx.compose.ui.platform.LocalContext.current))
    }
}