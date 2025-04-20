package com.example.restaurantmanage.ui.theme.screens.user.order

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme

@Composable
fun PaymentSuccessScreen(
    navController: NavHostController,
    orderId: String, // Mã đơn hàng
    customerName: String, // Tên khách hàng
    amount: String, // Số tiền
    paymentTime: String // Thời gian thanh toán
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))
        // Thông tin hóa đơn
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mã đơn hàng",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = orderId, // Dữ liệu động
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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

        
        Spacer(modifier = Modifier.height(24.dp))

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
                        popUpTo("introduce") {
                            inclusive = false
                        }
                    }
                },
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp), // Bo góc tròn
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCCCCCC), // Màu xám
                    contentColor = Color.Black // Chữ đen
                )
            ) {
                Text(
                    "Trở về trang chủ",
                    fontSize = 16.sp,
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
                    .width(180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp), // Bo góc tròn
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, // Màu đen
                    contentColor = Color.White // Chữ trắng
                )
            ) {
                Text(
                    "Đánh giá",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
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
            orderId = "abczyx", // Dữ liệu mẫu cho preview
            customerName = "Nguyen XX",
            amount = "100.000đ",
            paymentTime = "dd/mm/yyyy"
        )
    }
}