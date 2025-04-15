package com.example.restaurantmanage.ui.theme.screens.user.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.viewmodels.BookingViewModel
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.TableEntity
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

                if (numberOfGuests.toIntOrNull() ?: 0 > table.capacity) {
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
                    if ((customerName.isNotEmpty() && phoneNumber.isNotEmpty() && 
                        numberOfGuests.isNotEmpty()) && 
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
fun BookingScreen(navController: NavController) {
    val context = LocalContext.current
    val database = RestaurantDatabase.getDatabase(context)
    val viewModel: BookingViewModel = viewModel(
        factory = BookingViewModel.Factory(
            bookingDao = database.bookingDao(),
            tableDao = database.tableDao()
        )
    )

    val availableTables by viewModel.availableTables.collectAsState(initial = emptyList())
    val keyboardController = LocalSoftwareKeyboardController.current
    var textSearch by remember { mutableStateOf("") }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableEntity?>(null) }

    Scaffold(
        topBar = {
            AppBar(
                title = "Đặt bàn",
                navController = navController,
                showBackButton = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(
                value = textSearch,
                onValueChange = { textSearch = it },
                label = {
                    Text(
                        text = buildAnnotatedString {
                            append(
                                AnnotatedString(
                                    text = "Tìm bàn ",
                                    spanStyle = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                )
                            )
                            append(
                                AnnotatedString(
                                    text = "(phòng ăn)",
                                    spanStyle = SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                )
                            )
                        }
                    )
                },
                placeholder = {
                    Text(
                        text = "Thời gian • số lượng phòng • số người",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFA8A2A2)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (availableTables.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có bàn trống",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(availableTables) { table ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.table_image),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = table.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sức chứa: ${table.capacity} người",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "500,000 VND",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Button(
                                        onClick = {
                                            selectedTable = table
                                            showBookingDialog = true
                                        }
                                    ) {
                                        Text("Đặt bàn")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBookingDialog && selectedTable != null) {
        BookingDialog(
            table = selectedTable!!,
            onDismiss = {
                showBookingDialog = false
                selectedTable = null
            },
            onConfirm = { name, phone, guests, date, note ->
                viewModel.createBooking(
                    tableId = selectedTable!!.id,
                    customerName = name,
                    phoneNumber = phone,
                    numberOfGuests = guests,
                    time = date,
                    note = note
                )
                showBookingDialog = false
                selectedTable = null
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