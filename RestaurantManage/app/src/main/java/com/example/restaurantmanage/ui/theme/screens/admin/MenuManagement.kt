package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.PrimaryColor
import java.text.NumberFormat
import java.util.Locale

// Data classes để lưu trữ thông tin
data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val orderCount: Int,
    val inStock: Boolean,
    val imageUrl: String? = null
)

data class MenuCategory(
    val id: Int,
    val name: String,
    val items: List<MenuItem>
)

@Composable
fun MenuManagementScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Mock data
    val categories = listOf(
        MenuCategory(
            id = 1, 
            name = "Món phổ biến", 
            items = listOf(
                MenuItem(1, "Tôm xào chua ngọt", 150000.0, "Món chính", 120, true),
                MenuItem(2, "Cá hồi nướng", 180000.0, "Món chính", 98, true),
                MenuItem(3, "Gà nướng", 140000.0, "Món chính", 85, false)
            )
        ),
        MenuCategory(
            id = 2, 
            name = "Món chính", 
            items = listOf(
                MenuItem(4, "Bò xào nấm", 160000.0, "Món chính", 75, true),
                MenuItem(5, "Sườn xào chua ngọt", 120000.0, "Món chính", 82, true)
            )
        ),
        MenuCategory(
            id = 3, 
            name = "Món tráng miệng", 
            items = listOf(
                MenuItem(6, "Bánh flan", 25000.0, "Tráng miệng", 150, true),
                MenuItem(7, "Chè thái", 30000.0, "Tráng miệng", 125, true)
            )
        ),
        MenuCategory(
            id = 4, 
            name = "Đồ uống", 
            items = listOf(
                MenuItem(8, "Trà đào", 35000.0, "Đồ uống", 200, true),
                MenuItem(9, "Cà phê đen", 25000.0, "Đồ uống", 180, true),
                MenuItem(10, "Sinh tố xoài", 40000.0, "Đồ uống", 90, true)
            )
        )
    )
    
    // Lấy danh sách món ăn bán chạy
    val topSellingItems = categories.flatMap { it.items }
        .sortedByDescending { it.orderCount }
        .take(5)
    
    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ THỰC ĐƠN",
                navController = navController,
                onMenuClick = {
                    // Logic khi nhấn icon menu
                },
                onAvatarClick = {
                    // Logic khi nhấn icon avatar
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Thêm món mới */ },
                containerColor = PrimaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm món",
                    tint = Color.White
                )
            }
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
            // Phần thống kê món ăn bán chạy
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
                            text = "Món ăn bán chạy",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        topSellingItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(24.dp)
                                )
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                                            .format(item.price) + "đ",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Text(
                                    text = "${item.orderCount} đơn",
                                    fontSize = 14.sp,
                                    color = PrimaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            if (index < topSellingItems.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
            
            // Danh sách danh mục
            items(categories) { category ->
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(onClick = { /* Xem toàn bộ */ }) {
                        Text(
                            text = "Xem tất cả",
                            color = PrimaryColor,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Danh sách món ăn trong danh mục
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(category.items) { item ->
                        MenuItemCard(item = item)
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    var isOutOfStock by remember { mutableStateOf(!item.inStock) }
    
    Card(
        modifier = Modifier
            .width(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Phần ảnh món ăn (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Hiển thị "Hết hàng" nếu món không còn
                if (isOutOfStock) {
                    Surface(
                        modifier = Modifier
                            .matchParentSize(),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "HẾT HÀNG",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                        .format(item.price) + "đ",
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = !isOutOfStock,
                        onCheckedChange = { isOutOfStock = !it },
                        modifier = Modifier.size(30.dp)
                    )
                    
                    IconButton(
                        onClick = { /* Sửa món */ },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Sửa",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuManagementScreenPreview() {
    RestaurantManageTheme {
        MenuManagementScreen(navController = rememberNavController())
    }
} 