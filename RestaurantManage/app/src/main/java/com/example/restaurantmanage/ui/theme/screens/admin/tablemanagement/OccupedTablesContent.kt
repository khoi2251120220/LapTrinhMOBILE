package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.viewmodels.TableManagementViewModel

@Composable
fun OccupiedTablesContent(
    viewModel: TableManagementViewModel,
    onTableClick: (TableEntity) -> Unit
) {
    val occupiedTables by viewModel.occupiedTables.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (occupiedTables.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Không có bàn nào đang phục vụ",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(occupiedTables) { table ->
                AdminTableCard(
                    table = table,
                    onClick = { onTableClick(table) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}