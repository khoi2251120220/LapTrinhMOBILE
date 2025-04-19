package com.example.restaurantmanage.ui.theme.screens.user.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf(userProfile.name) }
    var email by remember { mutableStateOf(userProfile.email) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var address by remember { mutableStateOf(userProfile.address) }
    val favoriteItems = remember { mutableStateListOf<String>().apply { addAll(userProfile.favoriteItems) } }
    var newFavoriteItem by remember { mutableStateOf("") }

    // Cập nhật UI khi userProfile thay đổi
    LaunchedEffect(userProfile) {
        name = userProfile.name
        email = userProfile.email
        phone = userProfile.phone
        address = userProfile.address
        favoriteItems.clear()
        favoriteItems.addAll(userProfile.favoriteItems)
    }

    // Reset trạng thái lưu sau 2 giây
    if (isSaved) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            viewModel.resetSaveStatus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            // Đăng xuất
                            viewModel.signOut(context) {
                                // Khởi động lại MainActivity bằng Intent
                                val intent = android.content.Intent(context, com.example.restaurantmanage.MainActivity::class.java)
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or 
                                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Out",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hiển thị vai trò người dùng
            Text(
                text = "Vai trò: ${userProfile.role}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (userProfile.role == "admin") Color.Blue else Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị lỗi nếu có
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Xóa thông báo lỗi")
                }
            }

            // User Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; viewModel.updateProfile(name = it) },
                            label = { Text("Tên") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { /* Email không cho chỉnh sửa */ },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = false
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Phone
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it; viewModel.updateProfile(phone = it) },
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            enabled = !isLoading
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Address
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it; viewModel.updateProfile(address = it) },
                            label = { Text("Địa chỉ") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Favorites Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add new favorite
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newFavoriteItem,
                            onValueChange = { newFavoriteItem = it },
                            label = { Text("Thêm món yêu thích") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        )
                        IconButton(
                            onClick = {
                                if (newFavoriteItem.isNotBlank()) {
                                    favoriteItems.add(newFavoriteItem)
                                    viewModel.updateProfile(favoriteItems = favoriteItems.toList())
                                    newFavoriteItem = ""
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Favorite items list
                    if (favoriteItems.isEmpty()) {
                        Text(
                            "Chưa có món yêu thích",
                            modifier = Modifier.padding(8.dp),
                            color = Color.Gray
                        )
                    } else {
                        Column {
                            favoriteItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.LightGray)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(item, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            favoriteItems.remove(item)
                                            viewModel.updateProfile(favoriteItems = favoriteItems.toList())
                                        },
                                        enabled = !isLoading
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Xóa",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Order History Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Order History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Lịch sử gọi món của bạn sẽ xuất hiện ở đây",
                        modifier = Modifier.padding(8.dp),
                        color = Color.Gray
                    )
                }
            }
        }

        // Save Button - Cố định ở dưới màn hình
        Button(
            onClick = { if (!isLoading) viewModel.saveProfile() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSaved) Color.Green else MaterialTheme.colorScheme.primary
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = if (isSaved) "ĐÃ LƯU" else "LƯU THÔNG TIN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        val mockViewModel = ProfileViewModel().apply {
            updateProfile(
                name = "T4 dpcfso",
                email = "jul.msnr.nb@gmail.com",
                phone = "012416799",
                address = "192 Street Norms, Aqn.1, Flat 2",
                favoriteItems = listOf("Món yêu thích 1", "Món yêu thích 2"),
                role = "user"
            )
        }
        ProfileScreen(
            navController = NavController(LocalContext.current),
            viewModel = mockViewModel
        )
    }
}