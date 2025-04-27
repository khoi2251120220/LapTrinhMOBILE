package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.viewmodels.TableManagementViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsContent(
    viewModel: TableManagementViewModel,
    onBookingClick: (BookingEntity) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Date()) }
    val bookingsForDate by viewModel.bookingsForDate.collectAsState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        viewModel.loadBookingsForDate(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Date selector
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Chọn ngày: ${SimpleDateFormat("dd/MM/yyyy", Locale("vi")).format(selectedDate)}",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Bookings list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            if (bookingsForDate.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Không có đặt bàn nào trong ngày này",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(bookingsForDate) { booking ->
                    BookingListItem(
                        booking = booking,
                        onClick = { onBookingClick(booking) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply {
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
}