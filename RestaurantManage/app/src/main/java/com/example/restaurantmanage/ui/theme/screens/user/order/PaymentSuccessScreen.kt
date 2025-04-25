package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.viewmodels.OrderViewModel
import com.example.restaurantmanage.viewmodels.OrderViewModelFactory

@Composable
fun PaymentSuccessScreen(
    navController: NavHostController,
    orderId: String, // Mã đơn hàng
    customerName: String, // Tên khách hàng
    amount: String, // Số tiền
    paymentTime: String, // Thời gian thanh toán
    orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    )
) {
    // Lấy danh sách các món ăn trong đơn hàng
    val orderItems by orderViewModel.orderItems.collectAsState()
    
    // Kích hoạt việc tải dữ liệu khi màn hình được hiển thị
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderItems(orderId)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Tiêu đề
            Text(
                text = "Thanh toán thành công",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Hướng dẫn
            Text(
                text = "Hóa đơn của quý khách đã được thanh toán thành công",
                style = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
        
        // Thông tin hóa đơn
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mã đơn hàng",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Đơn hàng #${orderId}", // Thêm tiền tố cho rõ ràng
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tên khách hàng",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = customerName, // Dữ liệu động
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Danh sách các món ăn trong hóa đơn
                    if (orderItems.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )

                        Text(
                            text = "Chi tiết món",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Hiển thị từng món ăn 
                        orderItems.forEach { orderItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Text(
                                        text = "${orderItem.quantity}x",
                                        fontSize = 16.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = orderItem.menuItemName,
                                        fontSize = 16.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                Text(
                                    text = "${(orderItem.price * orderItem.quantity).toInt()} VNĐ",
                                    fontSize = 16.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Số tiền",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = amount, // Dữ liệu động
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thời gian thanh toán",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = paymentTime, // Dữ liệu động
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Nút "Trở về" và "Đánh giá"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Nút "Trở về trang chủ"
                Button(
                    onClick = {
                        // Điều hướng về trang chủ thay vì quay lại
                        navController.navigate("home") {
                            // Xóa tất cả các màn hình trước đó khỏi back stack
                            popUpTo("home") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp), // Bo góc tròn
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCCCCCC), // Màu xám
                        contentColor = Color.Black // Chữ đen
                    )
                ) {
                    Text(
                        "Trở về trang chủ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Nút "Đánh giá"
                Button(
                    onClick = {
                        // Điều hướng đến RatingScreen
                        navController.navigate("rating")
                    },
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp), // Bo góc tròn
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black, // Màu đen
                        contentColor = Color.White // Chữ trắng
                    )
                ) {
                    Text(
                        "Đánh giá",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentSuccessScreenPreview() {
    RestaurantManageTheme {
        val navController = rememberNavController()
        PaymentSuccessScreen(
            navController = navController,
            orderId = "123", // Dữ liệu mẫu cho preview
            customerName = "Nguyen Van A",
            amount = "100.000đ",
            paymentTime = "01/01/2023 15:30"
        )
    }
}