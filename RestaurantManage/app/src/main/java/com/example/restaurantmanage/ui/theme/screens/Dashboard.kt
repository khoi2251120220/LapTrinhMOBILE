package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.viewmodels.DashboardViewModel
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.SuccessColor
import com.example.restaurantmanage.ui.theme.TextColor
import java.util.Locale

@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel = DashboardViewModel()
    val revenueData by viewModel.data
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val chartColor = Color(0xFF1E88E5) // Màu xanh dương

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ DOANH THU",
                navController = navController,
                onMenuClick = {
                    // Logic khi nhấn icon menu (có thể mở drawer)
                },
                onAvatarClick = {
                    // Logic khi nhấn icon avatar (có thể mở profile)
                }
            )
        },
        bottomBar = {
            NavAdmin(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Thẻ Tổng doanh thu và Lợi nhuận
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Tổng doanh thu",
                                fontSize = 14.sp,
                                color = TextColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", revenueData.totalRevenue)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+${revenueData.revenueGrowth}% so với tháng trước",
                                color = SuccessColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Lợi nhuận",
                                fontSize = 14.sp,
                                color = TextColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = String.format(Locale.US, "%,d", revenueData.profit),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+${revenueData.profitGrowth}% so với tháng trước",
                                color = SuccessColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Biểu đồ doanh thu
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Biểu đồ doanh thu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val dailyRevenue: List<Pair<Int, Double>> = revenueData.dailyRevenue
                        val maxRevenue = dailyRevenue.maxOfOrNull { pair: Pair<Int, Double> -> pair.second } ?: 0.0
                        val minRevenue = dailyRevenue.minOfOrNull { pair: Pair<Int, Double> -> pair.second } ?: 0.0
                        val range = if (maxRevenue == minRevenue) 1.0 else maxRevenue - minRevenue

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height - 20.dp.toPx() // Để lại không gian cho nhãn
                            val pointSpacing = if (dailyRevenue.size > 1) width / (dailyRevenue.size - 1) else width // Tránh chia cho 0

                            // Vẽ nền mờ của biểu đồ
                            drawRect(
                                color = chartColor.copy(alpha = 0.1f),
                                topLeft = Offset(0f, 0f),
                                size = size.copy(height = height)
                            )

                            // Vẽ đường biểu đồ
                            val path = Path()
                            dailyRevenue.forEachIndexed { index, (day, revenue) ->
                                val x = index * pointSpacing
                                val normalizedRevenue = if (range == 0.0) 0.0 else ((revenue - minRevenue) / range) * (height - 50f)
                                val y = height - normalizedRevenue

                                if (index == 0) {
                                    path.moveTo(x, y.toFloat())
                                } else {
                                    path.lineTo(x, y.toFloat())
                                }

                                drawCircle(
                                    color = chartColor,
                                    radius = 5f,
                                    center = Offset(x, y.toFloat())
                                )

                                // Vẽ nhãn ngày (chỉ vẽ nếu có dữ liệu)
                                if (dailyRevenue.isNotEmpty()) {
                                    drawContext.canvas.nativeCanvas.apply {
                                        drawText(
                                            day.toString(),
                                            x,
                                            height + 20.dp.toPx(),
                                            android.graphics.Paint().apply {
                                                color = android.graphics.Color.BLACK
                                                textSize = 30f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                        )
                                    }
                                }
                            }

                            drawPath(
                                path,
                                chartColor,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                            )
                        }
                    }
                }
            }

            // Khách hàng quen thuộc
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Khách hàng quen thuộc",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        revenueData.loyalCustomers.forEach { customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Giả lập avatar (hình tròn)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Gray, shape = RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = customer[0].toString(), color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = customer,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${customer.lowercase(Locale.ROOT)}@gmail.com", // Sửa toLowerCase()
                                        fontSize = 12.sp,
                                        color = TextColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Khoảng cách cuối
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    RestaurantManageTheme {
        DashboardScreen(navController = rememberNavController())
    }
}