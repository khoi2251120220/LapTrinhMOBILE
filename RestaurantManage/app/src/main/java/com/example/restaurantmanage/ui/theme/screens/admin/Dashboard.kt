package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.viewmodels.DashboardViewModel
import com.example.restaurantmanage.viewmodels.DashboardViewModelFactory
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.SuccessColor
import com.example.restaurantmanage.ui.theme.TextColor
import java.util.Locale

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(context.applicationContext as android.app.Application)
    )
    
    val revenueData by viewModel.data
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val chartColor = Color(0xFF1E88E5)

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ DOANH THU",
                navController = navController,
                onMenuClick = {
                    // Logic khi nhấn icon menu (có thể mở drawer)
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
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, Color.LightGray)
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
                                text = "${String.format(Locale.US, "%,.1f", revenueData.totalRevenue / 1000000)} Triệu",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (revenueData.revenueGrowth > 0) "+${revenueData.revenueGrowth}% so với tháng trước" else "${revenueData.revenueGrowth}% so với tháng trước",
                                color = if (revenueData.revenueGrowth >= 0) SuccessColor else Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, Color.LightGray)
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
                                text = "${String.format(Locale.US, "%,.1f", revenueData.profit / 1000000.0)} Triệu",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (revenueData.profitGrowth > 0) "+${revenueData.profitGrowth}% so với tháng trước" else "${revenueData.profitGrowth}% so với tháng trước",
                                color = if (revenueData.profitGrowth >= 0) SuccessColor else Color.Red,
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
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Biểu đồ doanh thu (7 ngày gần nhất)",
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
                            val height = size.height - 20.dp.toPx()
                            val pointSpacing = if (dailyRevenue.size > 1) width / (dailyRevenue.size - 1) else width // Tránh chia cho 0

                            // Vẽ các đường lưới ngang cho biểu đồ
                            val gridLines = 5
                            val gridSpacing = height / gridLines
                            
                            for (i in 0..gridLines) {
                                val y = i * gridSpacing
                                
                                // Vẽ đường lưới
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.5f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1f
                                )
                                
                                // Vẽ giá trị tiền tương ứng trên trục Y theo đơn vị triệu
                                val value = maxRevenue - (i * (maxRevenue - minRevenue) / gridLines)
                                val valueInMillions = value / 1000000
                                
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        String.format(Locale.US, "%.1fM", valueInMillions),
                                        10f,
                                        y + 10,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 24f
                                            textAlign = android.graphics.Paint.Align.LEFT
                                        }
                                    )
                                }
                            }

                            // Vẽ nền mờ của biểu đồ
                            drawRect(
                                color = chartColor.copy(alpha = 0.1f),
                                topLeft = Offset(0f, 0f),
                                size = size.copy(height = height)
                            )

                            if (dailyRevenue.isNotEmpty()) {
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
                                    
                                    // Vẽ số ngày
                                    drawContext.canvas.nativeCanvas.apply {
                                        drawText(
                                            day.toString(),
                                            x,
                                            height + 20.dp.toPx(),
                                            android.graphics.Paint().apply {
                                                color = android.graphics.Color.BLACK
                                                textSize = 24f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                        )
                                    }
                                    
                                    // Hiển thị giá trị doanh thu theo triệu tại mỗi điểm
                                    val revenueInMillions = revenue / 1000000
                                    drawContext.canvas.nativeCanvas.apply {
                                        drawText(
                                            String.format(Locale.US, "%.1fM", revenueInMillions),
                                            x,
                                            y.toFloat() - 10,
                                            android.graphics.Paint().apply {
                                                color = android.graphics.Color.DKGRAY
                                                textSize = 20f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                        )
                                    }
                                }

                                // Vẽ vùng phía dưới đường biểu đồ
                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(width, height)
                                    lineTo(0f, height)
                                    close()
                                }
                                
                                drawPath(
                                    fillPath,
                                    chartColor.copy(alpha = 0.1f)
                                )

                                // Vẽ đường biểu đồ
                                drawPath(
                                    path,
                                    chartColor,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                                )
                            } else {
                                // Hiển thị thông báo khi không có dữ liệu
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        "Không có dữ liệu doanh thu",
                                        width / 2,
                                        height / 2,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 32f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
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
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Khách hàng quen thuộc",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (revenueData.loyalCustomers.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                items(revenueData.loyalCustomers) { customer ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color.Gray, shape = RoundedCornerShape(50)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = customer.firstOrNull()?.toString() ?: "?", 
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = customer,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Chưa có khách hàng",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } 
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