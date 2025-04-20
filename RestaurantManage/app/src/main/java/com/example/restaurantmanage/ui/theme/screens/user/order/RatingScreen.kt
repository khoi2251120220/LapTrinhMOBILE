package com.example.restaurantmanage.ui.theme.screens.user.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.RatingComponent
import com.example.restaurantmanage.viewmodels.RatingViewModel
import com.example.restaurantmanage.viewmodels.RatingViewModelFactory

@Composable
fun RatingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val ratingViewModel: RatingViewModel = viewModel(
        factory = RatingViewModelFactory(context.applicationContext as android.app.Application)
    )
    
    var rating by remember { mutableIntStateOf(0) } // Lưu số sao được chọn
    var feedback by remember { mutableStateOf("") } // Lưu nội dung phản hồi
    var isSubmitting by remember { mutableStateOf(false) } // Trạng thái đang gửi
    
    // Theo dõi trạng thái đánh giá đã được gửi
    var ratingSubmitted by remember { mutableStateOf(false) }
    
    // Hiển thị giao diện phù hợp dựa trên trạng thái đánh giá
    if (ratingSubmitted) {
        // Hiển thị màn hình cảm ơn sau khi đánh giá
        SuccessScreen(
            onBackToHome = {
                // Quay về trang chủ - navigation đúng
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
    } else {
        // Hiển thị màn hình đánh giá
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
                text = "Hãy để lại đánh giá và nhận xét để giúp chúng tôi cải thiện",
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
                    if (rating == 0) {
                        Toast.makeText(context, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isSubmitting = true
                    
                    // Sử dụng tên mặc định "Khách hàng"
                    ratingViewModel.submitRating("Khách hàng", rating, feedback)
                    
                    // Cập nhật UI ngay sau khi gửi đánh giá
                    ratingSubmitted = true
                },
                modifier = Modifier
                    .width(80.dp) // Nút tròn nhỏ
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp), // Bo góc tròn
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, // Nền đen
                    contentColor = Color.White // Chữ trắng
                ),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Gửi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessScreen(onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CẢM ƠN QUÝ KHÁCH!",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Đánh giá của quý khách đã được ghi nhận.\nChúng tôi rất trân trọng ý kiến đóng góp của quý khách để nâng cao chất lượng dịch vụ.",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                "Về trang chủ",
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

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    RestaurantManageTheme {
        SuccessScreen(onBackToHome = {})
    }
}