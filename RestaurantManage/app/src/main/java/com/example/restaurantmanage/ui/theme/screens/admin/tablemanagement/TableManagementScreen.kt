package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.viewmodels.TableManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(
    navController: NavController,
    viewModel: TableManagementViewModel = viewModel(
        factory = TableManagementViewModel.Factory(
            tableDao = RestaurantDatabase.getDatabase(LocalContext.current).tableDao(),
            bookingDao = RestaurantDatabase.getDatabase(LocalContext.current).bookingDao()
        )
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tab state
    val tabs = listOf("Tất cả bàn", "Lịch đặt bàn", "Bàn đang phục vụ")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Dialog state
    var showAddTableDialog by remember { mutableStateOf(false) }
    var showTableDetailsDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableEntity?>(null) }
    var showBookingDetailsDialog by remember { mutableStateOf(false) }

    // Table list based on selected tab
    val filteredTables by viewModel.getFilteredTables(selectedTabIndex).collectAsState(initial = emptyList())
    val selectedTableBooking by viewModel.selectedTableBooking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Refresh data when screen opens
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ BÀN",
                navController = navController,
                onMenuClick = { /* TODO */ },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTableDialog = true },
                containerColor = PrimaryColor,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Thêm bàn"
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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

                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> AllTablesContent(
                        tables = filteredTables,
                        onTableClick = { table ->
                            selectedTable = table
                            // Load bookings for this table if it's RESERVED
                            if (table.status == "RESERVED") {
                                viewModel.loadBookingForTable(table.id)
                            }
                            showTableDetailsDialog = true
                        }
                    )
                    1 -> BookingsContent(viewModel = viewModel) { booking ->
                        viewModel.selectBooking(booking)
                        showBookingDetailsDialog = true
                    }
                    2 -> OccupiedTablesContent(
                        viewModel = viewModel,
                        onTableClick = { table ->
                            selectedTable = table
                            showTableDetailsDialog = true
                        }
                    )
                }
            }
        }
    }

    // Show dialogs
    if (showAddTableDialog) {
        AddTableDialog(
            onDismiss = { showAddTableDialog = false },
            onAddTable = { name, capacity ->
                viewModel.addTable(name, capacity)
                showAddTableDialog = false
            }
        )
    }

    if (showTableDetailsDialog && selectedTable != null) {
        TableDetailsDialog(
            table = selectedTable!!,
            booking = selectedTableBooking,
            onDismiss = { showTableDetailsDialog = false },
            onStatusChange = { tableId, status ->
                viewModel.updateTableStatus(tableId, status)
                showTableDetailsDialog = false
            },
            onDeleteTable = { table ->
                viewModel.deleteTable(table)
                showTableDetailsDialog = false
            },
            onViewBookingDetails = {
                showBookingDetailsDialog = true
            },
            onCancelBooking = { booking ->
                viewModel.cancelBooking(booking)
                showTableDetailsDialog = false
            }
        )
    }

    if (showBookingDetailsDialog && selectedTableBooking != null) {
        BookingDetailsDialog(
            booking = selectedTableBooking!!,
            onDismiss = { showBookingDetailsDialog = false },
            onCancelBooking = { booking ->
                viewModel.cancelBooking(booking)
                showBookingDetailsDialog = false
                showTableDetailsDialog = false
            }
        )
    }
}

@Composable
fun AllTablesContent(
    tables: List<TableEntity>,
    onTableClick: (TableEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (tables.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Không có bàn nào",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(tables) { table ->
                AdminTableCard(
                    table = table,
                    onClick = { onTableClick(table) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}