package com.example.restaurantmanage.ui.theme.screens.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantmanage.R

@Composable
fun IntroduceScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hình ảnh minh họa (có thể là logo hoặc hình ảnh món ăn)
        Image(
            painter = painterResource(id = R.drawable.restaurant_logo),
            contentDescription = "Hình ảnh giới thiệu",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tiêu đề
        Text(
            text = "Chào mừng đến với Nhà hàng!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mô tả
        Text(
            text = "Khám phá thực đơn phong phú, đặt món dễ dàng và thưởng thức những bữa ăn tuyệt vời ngay hôm nay!",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Nút bắt đầu
        Button(
            onClick = {
                // Chuyển đến màn hình chính (HomeScreen)
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Bắt đầu ngay",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewIntroduceScreen() {
    IntroduceScreen(navController = NavController(LocalContext.current))
}