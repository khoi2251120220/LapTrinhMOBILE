package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.restaurantmanage.data.models.User
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.SuccessColor
import com.example.restaurantmanage.ui.theme.TextColor
import com.example.restaurantmanage.ui.theme.DangerColor

@Composable
fun UserManagementScreen(navController: NavController) {
    // Temporary mock data
    val users = remember {
        mutableStateOf(listOf(
            User(
                id = "1",
                name = "Nguyễn Văn A",
                email = "nguyenvana@gmail.com",
                phone = "0123456789",
                role = "ADMIN",
                status = "ACTIVE",
                createdAt = "01/01/2024",
                lastLogin = "10/03/2024"
            ),
            User(
                id = "2",
                name = "Trần Thị B",
                email = "tranthib@gmail.com",
                phone = "0987654321",
                role = "STAFF",
                status = "ACTIVE",
                createdAt = "15/02/2024",
                lastLogin = "09/03/2024"
            ),
            User(
                id = "3",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            ),
            User(
                id = "4",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            ),
            User(
                id = "5",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            ),
            User(
                id = "6",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            ),User(
                id = "7",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            ),
            User(
                id = "8",
                name = "Lê Văn C",
                email = "levanc@gmail.com",
                phone = "0369852147",
                role = "CUSTOMER",
                status = "INACTIVE",
                createdAt = "20/02/2024",
                lastLogin = "05/03/2024"
            )

        ))
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ NGƯỜI DÙNG",
                navController = navController,
                onMenuClick = { /* TODO */ },
                onAvatarClick = { /* TODO */ }
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
            items(users.value) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (user.status == "ACTIVE") SuccessColor else DangerColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = user.status,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Email: ${user.email}",
                                    fontSize = 14.sp,
                                    color = TextColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SĐT: ${user.phone}",
                                    fontSize = 14.sp,
                                    color = TextColor
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Vai trò: ${user.role}",
                                    fontSize = 14.sp,
                                    color = TextColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Đăng nhập: ${user.lastLogin}",
                                    fontSize = 12.sp,
                                    color = TextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

