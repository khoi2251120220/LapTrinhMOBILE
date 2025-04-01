package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AppBar

@Composable
fun FoodDetailScreen(
    foodName: String, // Tên món ăn
    priceRange: String, // Giá tiền
    description: String, // Mô tả món ăn
    navController: NavController // Thêm NavController để truyền vào AppBar
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                AppBar(
                    title = "Chi tiết món ăn",
                    navController = navController,
                    showBackButton = true
                )


                IconButton(
                    onClick = { navController.navigate("cart") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = Color.Black
                    )
                }
            }
        },
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
            foodName = "Tên món ăn",
            priceRange = "20.000 VNĐ - 30.000 VNĐ",
            description = "Gợi ý lựa chọn món, nguyên liệu, thành phần chính cơ bản, ...",
            navController = rememberNavController() // Sử dụng rememberNavController trong preview
        )
    }
}