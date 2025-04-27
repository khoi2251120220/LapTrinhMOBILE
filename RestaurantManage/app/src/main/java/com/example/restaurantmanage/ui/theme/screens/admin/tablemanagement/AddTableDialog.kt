package com.example.restaurantmanage.ui.theme.screens.admin.tablemanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTableDialog(
    onDismiss: () -> Unit,
    onAddTable: (String, Int, String) -> Unit
) {
    var tableName by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Thêm bàn mới",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = tableName,
                    onValueChange = { 
                        tableName = it
                        isError = false 
                    },
                    label = { Text("Tên bàn") },
                    isError = isError && tableName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (isError && tableName.isBlank()) {
                    Text(
                        text = "Tên bàn không được để trống",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { 
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            capacity = it
                            isError = false
                        }
                    },
                    label = { Text("Sức chứa") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = isError && (capacity.isBlank() || capacity.toIntOrNull() == 0),
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (isError && (capacity.isBlank() || capacity.toIntOrNull() == 0)) {
                    Text(
                        text = "Sức chứa phải lớn hơn 0",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = image,
                    onValueChange = { image = it },
                    label = { Text("Đường dẫn hình ảnh (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            isError = tableName.isBlank() || capacity.isBlank() || capacity.toIntOrNull() == 0
                            
                            if (!isError) {
                                onAddTable(
                                    tableName,
                                    capacity.toIntOrNull() ?: 0,
                                    image
                                )
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Thêm")
                    }
                }
            }
        }
    }
}