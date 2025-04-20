package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.viewmodels.MenuViewModel
import com.example.restaurantmanage.viewmodels.MenuViewModelFactory
import com.example.restaurantmanage.data.local.RestaurantDatabase
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale
import java.io.File
import android.util.Log
import com.example.restaurantmanage.util.DrawableResourceUtils

@Composable
fun MenuItemView(menuItem: MenuItemEntity, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                navController.navigate("detail/${menuItem.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Hình ảnh món ăn
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {

                
                // Debug thông tin hình ảnh để kiểm tra
                Log.d("MenuItemView", "Image path: ${menuItem.image}")
                
                if (menuItem.image.isNotEmpty() && !menuItem.image.startsWith("/")) {
                    // Sử dụng DrawableResourceUtils thay vì getIdentifier
                    val resourceId = DrawableResourceUtils.getDrawableResourceId(menuItem.image)
                    
                    if (resourceId != null) {
                        // Nếu là drawable resource
                        Log.d("MenuItemView", "Loading drawable resource: ${menuItem.image}, id: $resourceId")
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = menuItem.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        return@Box
                    }
                }
                
                // Nếu là đường dẫn file hoặc URL hoặc drawable không tìm thấy
                val imageModel = when {
                    // Kiểm tra xem đường dẫn có phải là đường dẫn file hoàn chỉnh không
                    menuItem.image.startsWith("/") -> {
                        val file = File(menuItem.image)
                        if (file.exists()) menuItem.image else "https://via.placeholder.com/200"
                    }
                    // Nếu không có hình ảnh hoặc không tìm thấy drawable
                    else -> "https://via.placeholder.com/200"
                }
                
                Log.d("MenuItemView", "Loading image from model: $imageModel")
                AsyncImage(
                    model = imageModel,
                    contentDescription = menuItem.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tên món
            Text(
                text = menuItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mô tả
            Text(
                text = menuItem.description.ifEmpty { "Món ăn đặc trưng" },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Giá
            if (menuItem.orderCount > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(menuItem.price),
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatPrice(menuItem.price * 0.75),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = formatPrice(menuItem.price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MenuScreen(
    navController: NavController,
    viewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(
            menuItemDao = RestaurantDatabase.getDatabase(LocalContext.current).menuItemDao(),
            categoryDao = RestaurantDatabase.getDatabase(LocalContext.current).categoryDao()
        )
    )
) {
    val categories by viewModel.categories.collectAsState()
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppBar("Thực đơn", navController, true)

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category.name,
                    isSelected = selectedCategory?.id == category.id,
                    onSelected = { selectedCategory = category }
                )
            }
        }

        selectedCategory?.let { category ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(category.items.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                MenuItemView(item.toMenuItemEntity(), navController)
                            }
                        }
                        if (rowItems.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onSelected() },
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getNumberInstance(Locale("vi", "VN"))
        .format(price) + "đ"
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    val context = LocalContext.current
    val database = RestaurantDatabase.getDatabase(context)
    MenuScreen(
        navController = rememberNavController(),
        viewModel = viewModel(
            factory = MenuViewModelFactory(
                menuItemDao = database.menuItemDao(),
                categoryDao = database.categoryDao()
            )
        )
    )
}