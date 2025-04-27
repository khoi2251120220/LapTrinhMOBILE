package com.example.restaurantmanage.ui.theme.screens.user.personal

import android.util.Log
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
import com.example.restaurantmanage.viewmodels.ProfileViewModel
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.OrderEntity
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.viewmodels.OrderViewModel
import com.example.restaurantmanage.viewmodels.OrderViewModelFactory
import com.example.restaurantmanage.viewmodels.BookingViewModel
import com.example.restaurantmanage.ui.theme.components.formatCurrency
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(RestaurantDatabase.getDatabase(LocalContext.current))
    ),
    orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    ),
    bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModel.Factory(
            bookingDao = RestaurantDatabase.getDatabase(LocalContext.current).bookingDao(),
            tableDao = RestaurantDatabase.getDatabase(LocalContext.current).tableDao()
        )
    )
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val orders by orderViewModel.userOrders.collectAsState(initial = emptyList())
    val bookings by bookingViewModel.userBookings.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var name by remember { mutableStateOf(userProfile.name) }
    var email by remember { mutableStateOf(userProfile.email) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var isEditing by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Lấy ID của người dùng hiện tại
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Load user orders
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            orderViewModel.loadUserOrders(userId)
        }
    }

    // Cập nhật UI khi userProfile thay đổi
    LaunchedEffect(userProfile) {
        name = userProfile.name
        email = userProfile.email
        phone = userProfile.phone
        nameError = null
        phoneError = null
    }

    // Reset trạng thái lưu sau 2 giây
    if (isSaved) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            viewModel.resetSaveStatus()
        }
    }

    // Hàm kiểm tra định dạng tên
    fun validateName(name: String): String? {
        return if (name.isBlank()) {
            "Vui lòng nhập tên"
        } else {
            null
        }
    }

    // Hàm kiểm tra định dạng số điện thoại
    fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Vui lòng nhập số điện thoại"
            !phone.startsWith("0") -> "Số điện thoại phải bắt đầu bằng 0"
            phone.length < 10 || phone.length > 11 -> "Số điện thoại phải có 10 hoặc 11 số"
            !phone.all { it.isDigit() } -> "Số điện thoại chỉ được chứa số"
            else -> null
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
                    text = "Hồ sơ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            viewModel.signOut(context) {
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
                        text = "Đăng xuất",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            // Thông tin người dùng
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thông tin cá nhân",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { isEditing = !isEditing }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = if (isEditing) "Kết thúc chỉnh sửa" else "Chỉnh sửa thông tin",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            if (isEditing) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { newName ->
                                        name = newName
                                        nameError = validateName(newName)
                                    },
                                    label = { Text("Tên") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = nameError != null,
                                    supportingText = {
                                        nameError?.let {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    enabled = !isLoading
                                )
                            } else {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Số điện thoại
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        if (isEditing) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { newPhone ->
                                    phone = newPhone
                                    phoneError = validatePhoneNumber(newPhone)
                                },
                                label = { Text("Số điện thoại") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                isError = phoneError != null,
                                supportingText = {
                                    phoneError?.let {
                                        Text(
                                            text = it,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                enabled = !isLoading
                            )
                        } else {
                            Text(
                                text = phone.ifEmpty { "Chưa cập nhật" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        Button(
                            onClick = {
                                nameError = validateName(name)
                                phoneError = validatePhoneNumber(phone)
                                if (nameError == null && phoneError == null) {
                                    viewModel.updateProfile(name = name, phone = phone)
                                    // Cập nhật số điện thoại lên Firestore
                                    FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                                        FirebaseFirestore.getInstance().collection("users").document(userId)
                                            .update("phone", phone)
                                            .addOnSuccessListener {
                                                // Cập nhật thành công
                                            }
                                            .addOnFailureListener { e ->
                                                // Xử lý lỗi
                                                viewModel.setError("Không thể cập nhật số điện thoại lên Firestore: ${e.message}")
                                                Log.e("ProfileScreen", "Không thể cập nhật số điện thoại: ${e.message}")
                                            }
                                    }
                                    isEditing = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = !isLoading && nameError == null && phoneError == null
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Lưu thay đổi")
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
                    Text(
                        text = "Lịch sử đặt hàng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (orders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bạn chưa có đơn hàng nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Column {
                            orders.forEach { order ->
                                OrderHistoryItem(order = order)
                                if (order != orders.last()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Booking History Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lịch sử đặt bàn",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (bookings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bạn chưa có đơn đặt bàn nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Column {
                            bookings.forEach { booking ->
                                BookingHistoryItem(
                                    booking = booking,
                                    onCancelClick = {
                                        bookingViewModel.cancelBooking(booking)
                                    }
                                )
                                if (booking != bookings.last()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success message
            if (isSaved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Thông tin đã được cập nhật",
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItem(order: OrderEntity) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Order ID and date
            Column {
                Text(
                    text = "Đơn hàng #${order.id.takeLast(5).uppercase()}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(order.orderDate),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Order status and total
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when(order.status) {
                        "PENDING" -> "Đang xử lý"
                        "COMPLETED" -> "Hoàn thành"
                        "CANCELLED" -> "Đã hủy"
                        else -> order.status
                    },
                    color = when(order.status) {
                        "PENDING" -> Color(0xFFFFA000)
                        "COMPLETED" -> Color(0xFF4CAF50)
                        "CANCELLED" -> Color(0xFFF44336)
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatCurrency(order.totalAmount),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BookingHistoryItem(
    booking: BookingEntity,
    onCancelClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Booking ID and date
            Column {
                Text(
                    text = "Bàn #${booking.tableId}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(booking.bookingTime),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Số người: ${booking.numberOfGuests}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Booking status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when(booking.status) {
                        "PENDING" -> "Chờ xác nhận"
                        "CONFIRMED" -> "Đã xác nhận"
                        "CANCELLED" -> "Đã hủy"
                        else -> booking.status
                    },
                    color = when(booking.status) {
                        "PENDING" -> Color(0xFFFFA000)
                        "CONFIRMED" -> Color(0xFF4CAF50)
                        "CANCELLED" -> Color(0xFFF44336)
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (booking.status == "CONFIRMED") {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Hủy đặt bàn", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Không thể xem trước màn hình hồ sơ do phụ thuộc vào cơ sở dữ liệu")
        }
    }
}