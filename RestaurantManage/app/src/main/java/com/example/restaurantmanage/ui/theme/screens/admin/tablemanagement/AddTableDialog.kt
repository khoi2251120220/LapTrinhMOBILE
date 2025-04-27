package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTableDialog(
    onDismiss: () -> Unit,
    onAddTable: (String, Int) -> Unit
) {
    var tableName by remember { mutableStateOf("") }
    var tableCapacity by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm bàn mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = tableName,
                    onValueChange = { tableName = it },
                    label = { Text("Tên bàn") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tableCapacity,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            tableCapacity = it
                        }
                    },
                    label = { Text("Sức chứa") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tableName.isNotEmpty() && tableCapacity.toIntOrNull() != null) {
                        onAddTable(tableName, tableCapacity.toInt())
                    }
                },
                enabled = tableName.isNotEmpty() && tableCapacity.toIntOrNull() != null
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}