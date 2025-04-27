package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.ui.theme.DangerColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingDetailsDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onCancelBooking: (BookingEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết đặt bàn") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Khách hàng:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = booking.customerName)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Số điện thoại:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = booking.phoneNumber)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Số lượng người:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "${booking.numberOfGuests} người")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Thời gian:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(booking.bookingTime)
                    )
                }

                if (booking.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ghi chú:",
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = booking.note,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCancelBooking(booking) },
                colors = ButtonDefaults.textButtonColors(contentColor = DangerColor)
            ) {
                Text("Hủy đặt bàn")
            }
        }
    )
}