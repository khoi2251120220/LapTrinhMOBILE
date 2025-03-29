package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.text.SimpleDateFormat
import java.util.*

// Enum để đại diện cho trạng thái bàn
enum class TableStatus {
    AVAILABLE, // Trống
    RESERVED,  // Đã đặt trước
    OCCUPIED   // Đang phục vụ
}

// Data classes
data class Table(
    val id: Int,
    val name: String,
    val capacity: Int,
    val status: TableStatus,
    val reservation: Reservation? = null,
    val currentOrder: Order? = null
)

data class Reservation(
    val id: Int,
    val customerName: String,
    val phoneNumber: String,
    val numberOfGuests: Int,
    val time: Date,
    val note: String = ""
)

data class Order(
    val id: Int,
    val startTime: Date,
    val items: List<String>,
    val totalAmount: Double,
    val status: String
)

@Composable
fun TableManagementScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Định dạng ngày giờ
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
    val timeFormatter = SimpleDateFormat("HH:mm", Locale("vi"))

    // Mock data
    val currentTime = Calendar.getInstance().time
    val calendar = Calendar.getInstance()

    // Thiết lập thời gian cho đặt bàn
    calendar.add(Calendar.HOUR, 1)
    val reservation1Time = calendar.time

    calendar.add(Calendar.HOUR, 1)
    val reservation2Time = calendar.time

    // Thiết lập thời gian bắt đầu cho order hiện tại
    calendar.add(Calendar.HOUR, -3)
    val order1StartTime = calendar.time

    val tables = listOf(
        Table(
            id = 1,
            name = "Bàn 01",
            capacity = 4,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 2,
            name = "Bàn 02",
            capacity = 2,
            status = TableStatus.OCCUPIED,
            currentOrder = Order(
                id = 1,
                startTime = order1StartTime,
                items = listOf("Tôm xào chua ngọt", "Cơm trắng", "Canh chua"),
                totalAmount = 250000.0,
                status = "Đang phục vụ"
            )
        ),
        Table(
            id = 3,
            name = "Bàn 03",
            capacity = 6,
            status = TableStatus.RESERVED,
            reservation = Reservation(
                id = 1,
                customerName = "Nguyễn Văn A",
                phoneNumber = "0912345678",
                numberOfGuests = 5,
                time = reservation1Time,
                note = "Kỷ niệm sinh nhật"
            )
        ),
        Table(
            id = 4,
            name = "Bàn 04",
            capacity = 4,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 5,
            name = "Bàn 05",
            capacity = 8,
            status = TableStatus.RESERVED,
            reservation = Reservation(
                id = 2,
                customerName = "Trần Thị B",
                phoneNumber = "0987654321",
                numberOfGuests = 7,
                time = reservation2Time,
                note = "Gần cửa sổ"
            )
        ),
        Table(
            id = 6,
            name = "Bàn 06",
            capacity = 2,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 7,
            name = "Bàn 07",
            capacity = 4,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 8,
            name = "Bàn VIP 01",
            capacity = 10,
            status = TableStatus.OCCUPIED,
            currentOrder = Order(
                id = 2,
                startTime = order1StartTime,
                items = listOf("Gà nướng", "Lẩu hải sản", "Rau muống xào", "Cơm trắng"),
                totalAmount = 850000.0,
                status = "Đang phục vụ"
            )
        ),
        Table(
            id = 9,
            name = "Bàn 09",
            capacity = 4,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 10,
            name = "Bàn 10",
            capacity = 6,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 11,
            name = "Bàn 11",
            capacity = 4,
            status = TableStatus.AVAILABLE
        ),
        Table(
            id = 12,
            name = "Bàn 12",
            capacity = 2,
            status = TableStatus.AVAILABLE
        )
    )

    // Lọc danh sách bàn theo trạng thái
    val availableTables = tables.filter { it.status == TableStatus.AVAILABLE }
    val reservedTables = tables.filter { it.status == TableStatus.RESERVED }
    val occupiedTables = tables.filter { it.status == TableStatus.OCCUPIED }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tất cả (${tables.size})", "Trống (${availableTables.size})",
        "Đã đặt (${reservedTables.size})", "Đang phục vụ (${occupiedTables.size})")

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ BÀN",
                navController = navController,
                onMenuClick = { /* Logic khi nhấn icon menu */ },
                onAvatarClick = { /* Logic khi nhấn icon avatar */ }
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
            // TabRow cho các tab lọc
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = PrimaryColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 2.dp,
                        color = PrimaryColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Hiển thị lưới bàn
            val filteredTables = when (selectedTab) {
                1 -> availableTables
                2 -> reservedTables
                3 -> occupiedTables
                else -> tables
            }

            if (filteredTables.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có bàn nào ở trạng thái này",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTables) { table ->
                        TableCard(
                            table = table,
                            dateFormatter = dateFormatter,
                            timeFormatter = timeFormatter,
                            onClick = { /* Logic xử lý khi nhấn vào bàn */ }
                        )
                    }
                }
            }

            // Phần danh sách đặt bàn sắp tới
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Đặt bàn sắp tới",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (reservedTables.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không có đặt bàn nào sắp tới",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            items(reservedTables) { table ->
                                val reservation = table.reservation
                                if (reservation != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFFFA000),
                                            modifier = Modifier.size(12.dp)
                                        ) {}

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = table.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = reservation.customerName,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AccessTime,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = timeFormatter.format(reservation.time),
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Icon(
                                                    imageVector = Icons.Default.CalendarMonth,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = dateFormatter.format(reservation.time),
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFE3F2FD),
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2196F3),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${reservation.numberOfGuests}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF2196F3),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    if (table != reservedTables.last()) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCard(
    table: Table,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    val cardColor = when (table.status) {
        TableStatus.AVAILABLE -> Color(0xFFE8F5E9)
        TableStatus.RESERVED -> Color(0xFFFFF8E1)
        TableStatus.OCCUPIED -> Color(0xFFFFEBEE)
    }

    val statusText = when (table.status) {
        TableStatus.AVAILABLE -> "Trống"
        TableStatus.RESERVED -> "Đã đặt"
        TableStatus.OCCUPIED -> "Đang phục vụ"
    }

    val statusColor = when (table.status) {
        TableStatus.AVAILABLE -> Color(0xFF4CAF50)
        TableStatus.RESERVED -> Color(0xFFFFA000)
        TableStatus.OCCUPIED -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = table.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${table.capacity} người",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth(),
                    color = statusColor,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (table.status) {
                TableStatus.RESERVED -> {
                    table.reservation?.let { reservation ->
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = reservation.customerName,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatter.format(reservation.time),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                TableStatus.OCCUPIED -> {
                    table.currentOrder?.let { order ->
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatter.format(order.startTime),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${order.items.size} món",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TableManagementScreenPreview() {
    RestaurantManageTheme {
        TableManagementScreen(navController = rememberNavController())
    }
}