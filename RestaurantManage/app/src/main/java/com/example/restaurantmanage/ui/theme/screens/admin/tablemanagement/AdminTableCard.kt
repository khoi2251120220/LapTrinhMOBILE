package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.ui.theme.DangerColor
import com.example.restaurantmanage.ui.theme.SuccessColor

@Composable
fun AdminTableCard(
    table: TableEntity,
    onClick: () -> Unit
) {
    val imageRes = when {
        table.capacity <= 2 -> R.drawable.table_2_seats
        table.capacity <= 4 -> R.drawable.table_4_seats
        table.capacity <= 6 -> R.drawable.table_6_seats
        else -> R.drawable.table_10_seats
    }

    val statusColor = when(table.status) {
        "AVAILABLE" -> SuccessColor
        "RESERVED" -> Color(0xFFFFA000)
        "OCCUPIED" -> DangerColor
        else -> Color.Gray
    }

    val statusText = when(table.status) {
        "AVAILABLE" -> "Trống"
        "RESERVED" -> "Đã đặt"
        "OCCUPIED" -> "Đang sử dụng"
        else -> table.status
    }

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
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Table image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Bàn ${table.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )

            // Table info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = table.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${table.capacity} người",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .background(
                        color = statusColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}