package com.example.restaurantmanage.ui.theme.screens.user.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.viewmodels.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    table: TableEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Date, String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var numberOfGuests by remember { mutableStateOf("1") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )

    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().apply { time = selectedDate }.get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().apply { time = selectedDate }.get(Calendar.MINUTE)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đặt bàn - ${table.name}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = customerName.isEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneNumber.isEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = numberOfGuests,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null)) {
                            numberOfGuests = it
                        }
                    },
                    label = { Text("Số lượng người") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = numberOfGuests.isEmpty() || ((numberOfGuests.toIntOrNull() ?: 0) > table.capacity)
                )

                if ((numberOfGuests.toIntOrNull() ?: 0) > table.capacity) {
                    Text(
                        text = "Số lượng người vượt quá sức chứa của bàn (${table.capacity} người)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Text("Chọn ngày")
                    }

                    OutlinedButton(
                        onClick = { showTimePicker = true }
                    ) {
                        Text("Chọn giờ")
                    }
                }

                Text(
                    text = "Thời gian: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi")).format(selectedDate)}",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isNotEmpty() && phoneNumber.isNotEmpty() && 
                        numberOfGuests.isNotEmpty() && 
                        ((numberOfGuests.toIntOrNull() ?: 0) <= table.capacity)) {
                        onConfirm(customerName, phoneNumber, numberOfGuests.toInt(), selectedDate, note)
                    }
                },
                enabled = (customerName.isNotEmpty() && phoneNumber.isNotEmpty() && 
                          numberOfGuests.isNotEmpty()) &&
                          ((numberOfGuests.toIntOrNull() ?: 0) <= table.capacity)
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply {
                                time = selectedDate
                                timeInMillis = millis
                            }
                            selectedDate = calendar.time
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            time = selectedDate
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        selectedDate = calendar.time
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Hủy")
                }
            },
            text = {
                TimePicker(
                    state = timePickerState
                )
            }
        )
    }
}

@Composable
fun TableCard(
    table: TableEntity,
    onClick: () -> Unit
) {
    // Determine the image resource based on table capacity
    val imageRes = when {
        table.capacity <= 2 -> R.drawable.table_2_seats
        table.capacity <= 4 -> R.drawable.table_4_seats
        table.capacity <= 6 -> R.drawable.table_6_seats
        else -> R.drawable.table_10_seats
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Image section with proper table image based on capacity
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Bàn ${table.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            
            // Text section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = table.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${table.capacity} người",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(36.dp)
                    ) {
                        Text("Chọn")
                    }
                }
            }
        }
    }
}


@Composable
fun BookingScreen(
    navController: NavController,
    viewModel: BookingViewModel = viewModel(
        factory = BookingViewModel.Factory(
            bookingDao = RestaurantDatabase.getDatabase(LocalContext.current).bookingDao(),
            tableDao = RestaurantDatabase.getDatabase(LocalContext.current).tableDao()
        )
    )
) {
    val tables by viewModel.availableTables.collectAsState(initial = emptyList())
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableEntity?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredTables by viewModel.filteredTables.collectAsState(initial = emptyList())

    // Initialize filtered tables with all available tables
    LaunchedEffect(Unit) {
        viewModel.setFilteredTables(tables)
    }
    
    // Update filtered tables when search query changes
    LaunchedEffect(searchQuery, tables) {
        if (searchQuery.isEmpty()) {
            viewModel.setFilteredTables(tables)
        } else {
            viewModel.searchTables(searchQuery)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            AppBar(
                title = "Đặt bàn",
                navController = navController,
                showBackButton = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Tìm kiếm bàn...") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            )


            // Table list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (filteredTables.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Không tìm thấy bàn nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(filteredTables) { table ->
                        TableCard(
                            table = table,
                            onClick = {
                                selectedTable = table
                                showBookingDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Booking Dialog
    if (showBookingDialog) {
        selectedTable?.let { table ->
            BookingDialog(
                table = table,
                onDismiss = { showBookingDialog = false },
                onConfirm = { customerName, phoneNumber, numberOfGuests, bookingTime, note ->
                    viewModel.createBooking(
                        tableId = table.id,
                        customerName = customerName,
                        phoneNumber = phoneNumber,
                        numberOfGuests = numberOfGuests,
                        bookingTime = bookingTime,
                        note = note
                    )
                    showBookingDialog = false
                    showSuccessMessage = true
                }
            )
        }
    }

    // Success message
    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessMessage = false },
            title = { Text("Đặt bàn thành công") },
            text = { Text("Bạn đã đặt bàn thành công. Chúng tôi sẽ liên hệ để xác nhận.") },
            confirmButton = {
                Button(onClick = { showSuccessMessage = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    RestaurantManageTheme {
        BookingScreen(navController = NavController(LocalContext.current))
    }
}