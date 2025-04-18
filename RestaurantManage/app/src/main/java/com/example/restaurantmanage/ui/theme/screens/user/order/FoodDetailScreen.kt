package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.viewmodels.MenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Composable
fun FoodDetailScreen(
    menuItemId: String,
    viewModel: MenuViewModel,
    navController: NavController
) {

    var menuItem by remember { mutableStateOf<MenuItemEntity?>(null) }


    LaunchedEffect(menuItemId) {
        viewModel.getMenuItemById(menuItemId).collect { item ->
            menuItem = item
        }
    }

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
                .background(Color.White)
                .padding(innerPadding)
        ) {

            menuItem?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.image),
                    contentDescription = "Hình ảnh ${it.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray) // Màu nền placeholder
                )


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = it.name,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )


                    Text(
                        text = "${it.price.toInt()} VNĐ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


                    Text(
                        text = "Mô tả",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )


                    Text(
                        text = it.description,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            // Xử lý thêm vào giỏ hàng (có thể gọi ViewModel hoặc điều hướng)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Thêm vào giỏ hàng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } ?: run {

                Text(text = "Loading...", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
