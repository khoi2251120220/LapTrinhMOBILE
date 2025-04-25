package com.example.restaurantmanage.ui.theme.screens.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.util.DrawableResourceUtils
import com.example.restaurantmanage.viewmodels.HomeViewModel
import com.example.restaurantmanage.viewmodels.HomeViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import android.util.Log

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    ),
) {
    val featuredItems by homeViewModel.featuredItems.collectAsState()
    val categories by homeViewModel.categories.collectAsState()
    val searchText = remember { mutableStateOf(TextFieldValue("")) }

    // Kích hoạt tải dữ liệu khi màn hình được hiển thị lần đầu
    LaunchedEffect(Unit) {
        homeViewModel.loadMenuItems()
        homeViewModel.loadCategories()
    }

    // Ghi log để debug
    Log.d("HomeScreen", "Số lượng danh mục: ${categories.size}")
    categories.forEach { category ->
        Log.d("HomeScreen", "Danh mục: ${category.name}, Số món: ${category.items.size}")
        category.items.forEach { item ->
            Log.d("HomeScreen", "  - Món: ${item.name}, Giá: ${item.price}, Hình: ${item.image}")
        }
    }

    Scaffold(
        topBar = {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                    homeViewModel.searchMenuItems(it.text)
                },
                placeholder = { Text("Tìm kiếm món ăn") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Phần hiển thị kết quả tìm kiếm
            if (searchText.value.text.isNotBlank()) {
                item {
                    Text(
                        text = if (categories.isNotEmpty()) "Kết quả tìm kiếm" else "Không tìm thấy món ăn",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                if (categories.isNotEmpty()) {
                    items(categories.flatMap { it.items }) { item ->
                        SimpleMenuItemCard(
                            name = item.name,
                            price = item.price,
                            imageRes = DrawableResourceUtils.getDrawableResourceId(item.image) ?: R.drawable.placeholder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            onItemClick = {
                                navController.navigate("detail/${item.id}")
                            }
                        )
                    }
                }
            } else {
                // Banner
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(R.drawable.featured_banner),
                        contentDescription = "Banner món nổi bật",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Món phổ biến (Featured Items)
                if (featuredItems.isNotEmpty()) {
                    item {
                        Text(
                            "Món phổ biến",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(featuredItems) { item ->
                                FeaturedItemCard(
                                    item = item,
                                    onItemClick = {
                                        if (item.inStock) {
                                            navController.navigate("detail/${item.id}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Danh sách món ăn với nút điều hướng
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Danh sách món ăn",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "Xem tất cả",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.navigate("menu") }
                        )
                    }
                }

                // Hiển thị danh sách món ăn theo dạng lưới 2 cột
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Lọc món ăn từ danh mục "Đồ ăn"
                        val foodCategory = categories.find { it.name == "Đồ ăn" }
                        val foodItems = foodCategory?.items?.take(2) ?: emptyList()
                        Log.d("HomeScreen", "Danh mục đồ ăn: ${foodCategory?.name}, Số món: ${foodItems.size}")

                        if (foodItems.isNotEmpty()) {
                            foodItems.forEach { item ->
                                SimpleMenuItemCard(
                                    name = item.name,
                                    price = item.price,
                                    imageRes = DrawableResourceUtils.getDrawableResourceId(item.image) ?: R.drawable.placeholder_food,
                                    modifier = Modifier.weight(1f),
                                    onItemClick = {
                                        navController.navigate("detail/${item.id}")
                                    }
                                )
                            }
                        } else {
                            // Nếu không có món ăn nào, hiển thị placeholder
                            SimpleMenuItemCard(
                                name = "Món ăn mẫu 1",
                                price = 150000.0,
                                imageRes = R.drawable.food1,
                                modifier = Modifier.weight(1f),
                                onItemClick = {}
                            )
                            SimpleMenuItemCard(
                                name = "Món ăn mẫu 2",
                                price = 100000.0,
                                imageRes = R.drawable.food2,
                                modifier = Modifier.weight(1f),
                                onItemClick = {}
                            )
                        }
                    }
                }

                // Danh sách thức uống với nút điều hướng
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Danh sách thức uống",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "Xem tất cả",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.navigate("menu") }
                        )
                    }
                }

                // Hiển thị danh sách thức uống theo dạng lưới 2 cột
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Lọc thức uống từ danh mục "Thức uống"
                        val drinkCategory = categories.find { it.name == "Thức uống" }
                        val drinkItems = drinkCategory?.items?.take(2) ?: emptyList()
                        Log.d("HomeScreen", "Danh mục thức uống: ${drinkCategory?.name}, Số món: ${drinkItems.size}")

                        if (drinkItems.isNotEmpty()) {
                            drinkItems.forEach { item ->
                                SimpleMenuItemCard(
                                    name = item.name,
                                    price = item.price,
                                    imageRes = DrawableResourceUtils.getDrawableResourceId(item.image) ?: R.drawable.drink,
                                    modifier = Modifier.weight(1f),
                                    onItemClick = {
                                        navController.navigate("detail/${item.id}")
                                    }
                                )
                            }
                        } else {
                            // Nếu không có thức uống nào, hiển thị placeholder
                            SimpleMenuItemCard(
                                name = "Nước ép dâu",
                                price = 30000.0,
                                imageRes = R.drawable.nuocepdau,
                                modifier = Modifier.weight(1f),
                                onItemClick = {}
                            )
                            SimpleMenuItemCard(
                                name = "Nước ép táo",
                                price = 25000.0,
                                imageRes = R.drawable.nuoceptao,
                                modifier = Modifier.weight(1f),
                                onItemClick = {}
                            )
                        }
                    }
                }

                // Khoảng cách dưới cùng
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun FeaturedItemCard(
    item: MenuItemEntity,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Tải hình ảnh với DrawableResourceUtils
                val imageRes = if (item.image.isNotEmpty()) {
                    DrawableResourceUtils.getDrawableResourceId(item.image) ?: R.drawable.placeholder
                } else {
                    R.drawable.placeholder
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (!item.inStock) {
                    Surface(
                        modifier = Modifier.matchParentSize(),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Hết",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun SimpleMenuItemCard(
    name: String,
    price: Double,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatPrice(price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        .format(price)
        .replace("₫", "VNĐ")
}