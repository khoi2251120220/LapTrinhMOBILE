package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.TopBar
import com.example.restaurantmanage.ui.theme.components.BottomNavBar

@Composable
fun FoodDetailScreen(
    onBackClick: () -> Unit, // Lambda cho hành động back
    onNavigate: (String) -> Unit, // Lambda cho hành động điều hướng
    foodName: String, // Tên món ăn
    priceRange: String, // Giá tiền
    description: String, // Mô tả món ăn
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Chi tiết món ăn",
                onBackClick = onBackClick, // Sử dụng lambda
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            BottomNavBar(
                onNavigate = onNavigate, // Sử dụng lambda thay vì NavHostController
                modifier = Modifier.fillMaxWidth(),
                navController = TODO(),
                currentRoute = TODO()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Nền trắng
                .padding(innerPadding) // Padding để tránh chồng lấn với TopBar và BottomNavBar
        ) {
            // Placeholder cho hình ảnh món ăn (sẽ thêm sau)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Chiều cao tương tự hình ảnh trong mẫu
                    .background(Color.LightGray) // Màu nền placeholder để dễ nhận biết
            ) {
                Text(
                    text = "Placeholder cho hình ảnh món ăn",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }

            // Nội dung chi tiết
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Tên món ăn
                Text(
                    text = foodName,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Giá tiền
                Text(
                    text = priceRange,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Mô tả
                Text(
                    text = "Mô tả",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Chi tiết mô tả
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Nút "Thêm vào giỏ hàng"
                Button(
                    onClick = {
                        // Xử lý thêm vào giỏ hàng (có thể gọi ViewModel hoặc điều hướng)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp), // Bo góc tròn
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50), // Màu xanh lá
                        contentColor = Color.White // Chữ trắng
                    )
                ) {
                    Text(
                        "Thêm vào giỏ hàng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FoodDetailScreenPreview() {
    RestaurantManageTheme {
        FoodDetailScreen(
            onBackClick = { /* Không làm gì trong preview */ },
            onNavigate = { /* Không làm gì trong preview */ },
            foodName = "Tên món ăn",
            priceRange = "20.000 VNĐ - 30.000 VNĐ",
            description = "Gợi ý lựa chọn món, nguyên liệu, thành phần chính cơ bản, ..."
        )
    }
}