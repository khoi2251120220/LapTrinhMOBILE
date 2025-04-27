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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val bookings by viewModel.bookingsForDate.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Date picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chọn ngày: ",
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale("vi")).format(selectedDate),
                    color = PrimaryColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Chọn ngày",
                    tint = PrimaryColor
                )
            }

            Button(
                onClick = { viewModel.loadBookingsForDate(selectedDate) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Làm mới"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Làm mới")
            }
        }

        // Booking list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (bookings.isEmpty()) {
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
                items(bookings) { booking ->
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
                            viewModel.loadBookingsForDate(selectedDate)
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