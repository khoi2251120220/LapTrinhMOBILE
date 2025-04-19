package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.viewmodels.TableManagementViewModel

@Composable
fun TableManagementScreen(navController: NavController) {
    val context = LocalContext.current
    val database = RestaurantDatabase.getDatabase(context)
    val viewModel: TableManagementViewModel = viewModel(
        factory = TableManagementViewModel.Factory(
            tableDao = database.tableDao(),
            bookingDao = database.bookingDao()
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTableDialog by remember { mutableStateOf(false) }

    val tables by viewModel.tables.collectAsState(initial = emptyList())
    val availableTables by viewModel.availableTables.collectAsState(initial = emptyList())
    val reservedTables by viewModel.reservedTables.collectAsState(initial = emptyList())
    val occupiedTables by viewModel.occupiedTables.collectAsState(initial = emptyList())

    val tabs = listOf(
        "Tất cả (${tables.size})",
        "Trống (${availableTables.size})",
        "Đã đặt (${reservedTables.size})",
        "Đang phục vụ (${occupiedTables.size})"
    )

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ BÀN",
                navController = navController,
                onMenuClick = { /* Logic khi nhấn icon menu */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTableDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm bàn")
            }
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

            val filteredTables = when (selectedTab) {
                0 -> tables
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
                            onClick = {
                                // Xử lý khi nhấn vào bàn
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddTableDialog) {
        var tableName by remember { mutableStateOf("") }
        var capacity by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTableDialog = false },
            title = { Text("Thêm bàn mới") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tableName,
                        onValueChange = { tableName = it },
                        label = { Text("Tên bàn") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) capacity = it },
                        label = { Text("Sức chứa") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tableName.isNotEmpty() && capacity.isNotEmpty()) {
                            viewModel.addTable(tableName, capacity.toInt())
                            showAddTableDialog = false
                        }
                    }
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTableDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun TableCard(
    table: TableEntity,
    onClick: () -> Unit
) {
    val cardColor = when (table.status) {
        "AVAILABLE" -> Color(0xFFE8F5E9)
        "RESERVED" -> Color(0xFFFFF8E1)
        "OCCUPIED" -> Color(0xFFFFEBEE)
        else -> Color(0xFFE8F5E9)
    }

    val statusText = when (table.status) {
        "AVAILABLE" -> "Trống"
        "RESERVED" -> "Đã đặt"
        "OCCUPIED" -> "Đang phục vụ"
        else -> "Trống"
    }

    val statusColor = when (table.status) {
        "AVAILABLE" -> Color(0xFF4CAF50)
        "RESERVED" -> Color(0xFFFFA000)
        "OCCUPIED" -> Color(0xFFF44336)
        else -> Color(0xFF4CAF50)
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