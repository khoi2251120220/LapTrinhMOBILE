package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.ui.theme.DangerColor
import com.example.restaurantmanage.ui.theme.SuccessColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TableDetailsDialog(
    table: TableEntity,
    booking: BookingEntity?,
    onDismiss: () -> Unit,
    onStatusChange: (Int, String) -> Unit,
    onDeleteTable: (TableEntity) -> Unit,
    onViewBookingDetails: () -> Unit,
    onCancelBooking: (BookingEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết bàn ${table.name}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val imageRes = when {
                    table.capacity <= 2 -> R.drawable.table_2_seats
                    table.capacity <= 4 -> R.drawable.table_4_seats
                    table.capacity <= 6 -> R.drawable.table_6_seats
                    else -> R.drawable.table_10_seats
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Bàn ${table.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tên bàn:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = table.name)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sức chứa:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "${table.capacity} người")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trạng thái:",
                        fontWeight = FontWeight.Medium
                    )

                    val statusColor = when(table.status) {
                        "AVAILABLE" -> SuccessColor
                        "RESERVED" -> Color(0xFFFFA000)
                        "OCCUPIED" -> DangerColor
                        else -> Color.Gray
                    }

                    Text(
                        text = when(table.status) {
                            "AVAILABLE" -> "Trống"
                            "RESERVED" -> "Đã đặt"
                            "OCCUPIED" -> "Đang sử dụng"
                            else -> table.status
                        },
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Change status section
                Text(
                    text = "Thay đổi trạng thái:",
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onStatusChange(table.id, "AVAILABLE") },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                        modifier = Modifier.weight(1f),
                        enabled = table.status != "AVAILABLE"
                    ) {
                        Text("Trống")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onStatusChange(table.id, "OCCUPIED") },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                        modifier = Modifier.weight(1f),
                        enabled = table.status != "OCCUPIED"
                    ) {
                        Text("Sử dụng")
                    }
                }

                // If table is reserved, show the booking details
                if (table.status == "RESERVED" && booking != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewBookingDetails() },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Thông tin đặt bàn",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

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

                            Spacer(modifier = Modifier.height(4.dp))

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

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { onCancelBooking(booking) },
                                colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Hủy đặt bàn")
                            }
                        }
                    }
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
                onClick = { onDeleteTable(table) },
                colors = ButtonDefaults.textButtonColors(contentColor = DangerColor)
            ) {
                Text("Xóa bàn")
            }
        }
    )
}