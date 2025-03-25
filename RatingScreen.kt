package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.restaurantmanage.ui.theme.components.RatingComponent

@Composable
fun RatingScreen(navController: NavHostController) {
    var rating by remember { mutableStateOf(0) } // Lưu số sao được chọn
    var feedback by remember { mutableStateOf("") } // Lưu nội dung phản hồi

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Nền trắng
            .padding(horizontal = 32.dp), // Padding ngang giống trong hình
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tiêu đề
        Text(
            text = "CẢM NHẬN CỦA QUÝ KHÁCH VỀ CỬA HÀNG CỦA CHÚNG TÔI ",
            style = TextStyle(
                fontSize = 20.sp,
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
            text = "Hãy để lại đánh giá và nhận xét đê giúp chúng tôi cải thiện",
            style = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Hệ thống đánh giá sao (dùng RatingComponent)
        RatingComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            onRatingChanged = { newRating ->
                rating = newRating
            }
        )

        // TextField để nhập phản hồi
        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            placeholder = {
                Text(
                    "Chia sẻ nhận xét của bạn với mọi người ...",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 24.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        // Nút Gửi
        Button(
            onClick = {
                // Xử lý gửi đánh giá (có thể điều hướng hoặc lưu dữ liệu)
                // Ví dụ: navController.navigate("user") để quay lại MainScreen
            },
            modifier = Modifier
                .width(80.dp) // Nút tròn nhỏ
                .height(40.dp),
            shape = RoundedCornerShape(20.dp), // Bo góc tròn
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black, // Nền đen
                contentColor = Color.White // Chữ trắng
            )
        ) {
            Text(
                "Gửi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingScreenPreview() {
    RestaurantManageTheme {
        val navController = rememberNavController()
        RatingScreen(navController = navController)
    }
}