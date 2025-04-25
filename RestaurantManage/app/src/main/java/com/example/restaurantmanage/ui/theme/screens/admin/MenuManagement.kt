package com.example.restaurantmanage.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.models.MenuItem
import com.example.restaurantmanage.ui.theme.components.AdminAppBar
import com.example.restaurantmanage.ui.theme.components.NavAdmin
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme
import com.example.restaurantmanage.ui.theme.PrimaryColor
import com.example.restaurantmanage.viewmodels.MenuViewModel
import com.example.restaurantmanage.viewmodels.MenuViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import com.example.restaurantmanage.ui.theme.components.ImagePicker
import androidx.compose.ui.res.painterResource
import com.example.restaurantmanage.util.DrawableResourceUtils


@Composable
fun MenuManagementScreen(
    navController: NavController,
    viewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(
            menuItemDao = RestaurantDatabase.getDatabase(LocalContext.current).menuItemDao(),
            categoryDao = RestaurantDatabase.getDatabase(LocalContext.current).categoryDao()
        )
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val categories by viewModel.categories.collectAsState()
    val topSellingItems by viewModel.topSellingItems.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<MenuItem?>(null) }
    
    if (showAddDialog) {
        MenuItemDialog(
            isEdit = false,
            item = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, price, category, imagePath, description ->
                viewModel.addMenuItem(name, price, category, imagePath, description)
                showAddDialog = false
            },
            viewModel = viewModel
        )
    }
    
    if (showEditDialog && selectedItem != null) {
        MenuItemDialog(
            isEdit = true,
            item = selectedItem,
            onDismiss = { showEditDialog = false },
            onSave = { name, price, category, imagePath, description ->
                viewModel.updateMenuItem(selectedItem!!.id, name, price, category, imagePath, description)
                showEditDialog = false
                selectedItem = null
            },
            viewModel = viewModel
        )
    }
    
    Scaffold(
        topBar = {
            AdminAppBar(
                title = "QUẢN LÝ THỰC ĐƠN",
                navController = navController,
                onMenuClick = {
                    // Logic khi nhấn icon menu
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm món",
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            NavAdmin(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Phần thống kê món ăn bán chạy
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TopSellingItemsCard(topSellingItems)
            }
            
            // Danh sách danh mục
            items(categories) { category ->
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.name,   
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(onClick = { /* Xem toàn bộ */ }) {
                        Text(
                            text = "Xem tất cả",
                            color = PrimaryColor,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Danh sách món ăn trong danh mục
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(category.items) { item ->
                        MenuItemCard(
                            item = item,
                            onStockChange = { inStock -> 
                                viewModel.updateItemStock(item.id, inStock)
                            },
                            onEditClick = {
                                selectedItem = item
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TopSellingItemsCard(topSellingItems: List<MenuItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Món ăn bán chạy",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            topSellingItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                                .format(item.price) + "đ",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Text(
                        text = "${item.orderCount} đơn",
                        fontSize = 14.sp,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (index < topSellingItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onStockChange: (Boolean) -> Unit,
    onEditClick: () -> Unit
) {
    var isOutOfStock by remember { mutableStateOf(!item.inStock) }

    Card(
        modifier = Modifier
            .width(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Hiển thị ảnh từ đường dẫn
                if (item.image.isNotEmpty() && !item.image.startsWith("/")) {
                    // Sử dụng DrawableResourceUtils
                    val resourceId = DrawableResourceUtils.getDrawableResourceId(item.image)
                    
                    if (resourceId != null) {
                        // Nếu là drawable resource
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Hiển thị ảnh placeholder nếu không tìm thấy drawable
                        AsyncImage(
                            model = "https://via.placeholder.com/200",
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (item.image.startsWith("/")) {
                    // Nếu là đường dẫn file
                    val file = File(item.image)
                    AsyncImage(
                        model = if (file.exists()) item.image else "https://via.placeholder.com/200",
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Nếu không có ảnh
                    AsyncImage(
                        model = "https://via.placeholder.com/200",
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (isOutOfStock) {
                    Surface(
                        modifier = Modifier.matchParentSize(),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "HẾT HÀNG",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                        .format(item.price) + "đ",
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = !isOutOfStock,
                        onCheckedChange = { 
                            isOutOfStock = !it
                            onStockChange(it)
                        },
                        modifier = Modifier.size(30.dp)
                    )
                    
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Sửa",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemDialog(
    isEdit: Boolean,
    item: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double, category: String, imagePath: String?, description: String) -> Unit,
    viewModel: MenuViewModel
) {
    var itemName by remember { mutableStateOf(item?.name ?: "") }
    var itemPrice by remember { mutableStateOf(item?.price?.toString() ?: "") }
    var selectedCategoryId by remember { mutableStateOf(item?.categoryId?.toString() ?: "") }
    var itemDescription by remember { mutableStateOf(item?.description ?: "") }
    var imagePath by remember { mutableStateOf(item?.image ?: "") }
    var hasError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDrawableDialog by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState()
    
    // Dialog chọn ảnh từ drawable
    if (showDrawableDialog) {
        DrawablePickerDialog(
            onDismiss = { showDrawableDialog = false },
            onImageSelected = { drawableName ->
                imagePath = drawableName
                showDrawableDialog = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (isEdit) "Chỉnh sửa món" else "Thêm món mới") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Phần chọn ảnh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hiển thị ảnh đã chọn hoặc ImagePicker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImagePicker(
                            currentImagePath = imagePath,
                            onImageSelected = { path ->
                                imagePath = path ?: ""
                            }
                        )
                        
                        Text("Từ thiết bị", fontSize = 12.sp)
                    }
                    
                    // Nút chọn ảnh từ drawable
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { showDrawableDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Chọn từ ứng dụng")
                        }
                        
                        Text("Từ drawable", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Phần hiển thị ảnh đã chọn (nếu là drawable)
                if (imagePath.isNotEmpty() && !imagePath.startsWith("/")) {
                    // Sử dụng DrawableResourceUtils thay vì getIdentifier
                    val resourceId = DrawableResourceUtils.getDrawableResourceId(imagePath)
                    
                    if (resourceId != null) {
                        Text(
                            "Ảnh đã chọn: $imagePath",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Tên món") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasError && itemName.isEmpty()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Giá (VND)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasError && (itemPrice.isEmpty() || itemPrice.toDoubleOrNull() == null)
                )
                
                if (!isEdit) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = categories.find { it.id.toString() == selectedCategoryId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Danh mục") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) 
                                            Icons.Default.KeyboardArrowUp 
                                        else 
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Chọn danh mục"
                                    )
                                }
                            },
                            isError = hasError && selectedCategoryId.isEmpty()
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(IntrinsicSize.Min)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id.toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                if (hasError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vui lòng điền đầy đủ thông tin",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemName.isNotEmpty() && itemPrice.isNotEmpty() && 
                        (isEdit || selectedCategoryId.isNotEmpty()) && 
                        itemPrice.toDoubleOrNull() != null
                    ) {
                        onSave(
                            itemName,
                            itemPrice.toDoubleOrNull() ?: 0.0,
                            if (isEdit) item?.categoryId?.toString() ?: "" else selectedCategoryId,
                            imagePath.ifEmpty { null },
                            itemDescription
                        )
                    } else {
                        hasError = true
                    }
                }
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

/**
 * Dialog hiển thị danh sách hình ảnh có sẵn trong drawable
 */
@Composable
fun DrawablePickerDialog(
    onDismiss: () -> Unit,
    onImageSelected: (String) -> Unit
) {

    
    // Sử dụng danh sách drawable từ DrawableResourceUtils
    val drawableNames = DrawableResourceUtils.availableDrawables
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn hình ảnh") },
        text = {
            LazyColumn {
                items(drawableNames.chunked(3)) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { drawableName ->
                            // Sử dụng DrawableResourceUtils
                            val resourceId = DrawableResourceUtils.getDrawableResourceId(drawableName)
                            
                            if (resourceId != null) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray)
                                        .clickable{ onImageSelected(drawableName) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = resourceId),
                                        contentDescription = drawableName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MenuManagementScreenPreview() {
    RestaurantManageTheme {
        MenuManagementScreen(navController = rememberNavController())
    }
} 