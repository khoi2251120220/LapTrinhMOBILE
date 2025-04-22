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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.restaurantmanage.data.models.User
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.SuccessColor
import com.example.restaurantmanage.ui.theme.TextColor
import com.example.restaurantmanage.ui.theme.DangerColor
import com.example.restaurantmanage.viewmodels.UserManagementViewModel
import com.example.restaurantmanage.viewmodels.UserManagementViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun UserManagementScreen(navController: NavController) {
    val viewModel: UserManagementViewModel = viewModel(factory = UserManagementViewModelFactory())
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showChangeRoleDialog by remember { mutableStateOf(false) }
    var showChangeStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                            }
                        )
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
    }
}

@Composable
fun UserCard(
    user: User,
    onEditRole: () -> Unit,
    onEditStatus: () -> Unit,
    onDelete: () -> Unit
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
            
            // Nút xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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

