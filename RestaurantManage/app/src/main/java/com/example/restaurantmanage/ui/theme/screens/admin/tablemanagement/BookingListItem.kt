package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.ui.theme.SuccessColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingListItem(
    booking: BookingEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(booking.bookingTime),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryColor
                )

                Text(
                    text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(booking.bookingTime),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = booking.customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Bàn ${booking.tableId} - ${booking.numberOfGuests} người",
                    fontSize = 14.sp,
                )

                Text(
                    text = booking.phoneNumber,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        color = if (booking.status == "CONFIRMED") SuccessColor.copy(alpha = 0.2f) else Color(0xFFFFA000).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (booking.status == "CONFIRMED") "Xác nhận" else "Chờ xử lý",
                    color = if (booking.status == "CONFIRMED") SuccessColor else Color(0xFFFFA000),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}