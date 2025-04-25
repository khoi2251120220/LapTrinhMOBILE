package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.OrderEntity
import com.example.restaurantmanage.data.models.User
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.SuccessColor
import com.example.restaurantmanage.ui.theme.TextColor
import com.example.restaurantmanage.ui.theme.DangerColor
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.viewmodels.OrderViewModel
import com.example.restaurantmanage.viewmodels.OrderViewModelFactory
import com.example.restaurantmanage.viewmodels.UserManagementViewModel
import com.example.restaurantmanage.viewmodels.UserManagementViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserManagementScreen(navController: NavController) {
    val viewModel: UserManagementViewModel = viewModel(factory = UserManagementViewModelFactory())
    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(RestaurantDatabase.getDatabase(context))
    )
    
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val orderItems by orderViewModel.orderItems.collectAsState()
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showChangeRoleDialog by remember { mutableStateOf(false) }
    var showChangeStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOrderDetails by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tab state
    val tabs = listOf("Quản lý người dùng", "Lịch sử đơn hàng")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Hiển thị lỗi nếu có
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ NGƯỜI DÙNG",
                navController = navController,
                onMenuClick = { /* TODO */ },
                actions = {
                    IconButton(onClick = { viewModel.loadUsers() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavAdmin(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryColor
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
            
            when (selectedTabIndex) {
                0 -> {
                    // User Management Tab
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(users) { user ->
                                    UserCard(
                                        user = user,
                                        onEditRole = {
                                            selectedUser = user
                                            showChangeRoleDialog = true
                                        },
                                        onEditStatus = {
                                            selectedUser = user
                                            showChangeStatusDialog = true
                                        },
                                        onDelete = {
                                            selectedUser = user
                                            showDeleteDialog = true
                                        },
                                        onOrdersClick = {
                                            // Switch to orders tab and load user's orders
                                            selectedTabIndex = 1
                                            user.id.let { userId -> orderViewModel.loadUserOrders(userId) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Order History Tab
                    val userOrders by orderViewModel.userOrders.collectAsState()
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (userOrders.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (selectedUser != null) 
                                        "Người dùng ${selectedUser?.name} chưa có đơn hàng nào" 
                                    else 
                                        "Chọn người dùng để xem lịch sử đơn hàng",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                                
                                if (selectedUser != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { selectedTabIndex = 0 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryColor
                                        )
                                    ) {
                                        Text("Quay lại danh sách người dùng")
                                    }
                                }
                            }
                        } else {
                            // Display orders with enhanced details
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Show user name as header
                                Text(
                                    text = selectedUser?.name ?: "Lịch sử đơn hàng",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                LazyColumn {
                                    items(userOrders) { order ->
                                        OrderHistoryCard(
                                            order = order,
                                            onClick = {
                                                selectedOrder = order
                                                // Load order details when clicked
                                                orderViewModel.loadOrderItems(order.id)
                                                showOrderDetails = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Dialog đổi vai trò
        if (showChangeRoleDialog && selectedUser != null) {
            val roles = listOf("ADMIN", "USER")
            var selectedRole by remember { mutableStateOf(selectedUser?.role ?: "CUSTOMER") }
            
            AlertDialog(
                onDismissRequest = { showChangeRoleDialog = false },
                title = { Text("Đổi vai trò") },
                text = {
                    Column {
                        Text("Chọn vai trò cho ${selectedUser?.name}")
                        Spacer(modifier = Modifier.height(16.dp))
                        roles.forEach { role ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = role == selectedRole,
                                    onClick = { selectedRole = role }
                                )
                                Text(text = role, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedUser?.id?.let { userId ->
                                viewModel.updateUserRole(userId, selectedRole)
                                showChangeRoleDialog = false
                            }
                        }
                    ) {
                        Text("Lưu")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangeRoleDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
        
        // Dialog đổi trạng thái
        if (showChangeStatusDialog && selectedUser != null) {
            val statuses = listOf("ACTIVE", "INACTIVE")
            var selectedStatus by remember { mutableStateOf(selectedUser?.status ?: "ACTIVE") }
            
            AlertDialog(
                onDismissRequest = { showChangeStatusDialog = false },
                title = { Text("Đổi trạng thái") },
                text = {
                    Column {
                        Text("Chọn trạng thái cho ${selectedUser?.name}")
                        Spacer(modifier = Modifier.height(16.dp))
                        statuses.forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = status == selectedStatus,
                                    onClick = { selectedStatus = status }
                                )
                                Text(text = status, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedUser?.id?.let { userId ->
                                viewModel.updateUserStatus(userId, selectedStatus)
                                showChangeStatusDialog = false
                            }
                        }
                    ) {
                        Text("Lưu")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangeStatusDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
        
        // Dialog xác nhận xóa
        if (showDeleteDialog && selectedUser != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn xóa người dùng ${selectedUser?.name}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedUser?.id?.let { userId ->
                                viewModel.deleteUser(userId)
                                showDeleteDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerColor
                        )
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
        
        // Dialog chi tiết đơn hàng
        if (showOrderDetails && selectedOrder != null) {
            AlertDialog(
                onDismissRequest = { showOrderDetails = false },
                title = { Text("Chi tiết đơn hàng #${selectedOrder?.id}") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Khách hàng: ${selectedOrder?.customerName}",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi"))
                        val orderDate = selectedOrder?.orderDate
                        
                        Text(
                            text = "Ngày đặt: ${orderDate?.let { dateFormat.format(it) } ?: "N/A"}",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Trạng thái: ${selectedOrder?.status}",
                            fontSize = 14.sp,
                            color = when (selectedOrder?.status) {
                                "COMPLETED" -> SuccessColor
                                "CANCELLED" -> DangerColor
                                else -> Color.Gray
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Các món đã đặt:",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Display order items with details
                        if (orderItems.isEmpty()) {
                            Text(
                                text = "Đang tải thông tin món...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        } else {
                            orderItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${item.quantity}x",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = item.menuItemName,
                                            fontSize = 14.sp
                                        )
                                    }
                                    
                                    Text(
                                        text = formatCurrency(item.price * item.quantity),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng cộng:",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatCurrency(selectedOrder?.totalAmount ?: 0.0),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showOrderDetails = false }) {
                        Text("Đóng")
                    }
                },
                dismissButton = {}
            )
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEditRole: () -> Unit,
    onEditStatus: () -> Unit,
    onDelete: () -> Unit,
    onOrdersClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header với tên người dùng và trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = if (user.status == "ACTIVE") SuccessColor else DangerColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onEditStatus() }
                ) {
                    Text(
                        text = user.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Thông tin chi tiết - tất cả trong một cột
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Email
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Email: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextColor
                    )
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = TextColor
                    )
                }
                
                //role
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onEditRole() }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Vai trò: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextColor
                    )
                    Text(
                        text = user.role,
                        fontSize = 14.sp,
                        color = TextColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa vai trò",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Row for actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View orders button
                Button(
                    onClick = onOrdersClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Xem đơn hàng", fontSize = 12.sp)
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = DangerColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Order ID and date
                Column {
                    Text(
                        text = "Đơn hàng #${order.id}",
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
            
            // Hint to click for more
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nhấn để xem chi tiết",
                    fontSize = 12.sp,
                    color = PrimaryColor
                )
            }
        }
    }
}

// Utility function to format currency
private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("₫", "VNĐ")
}

