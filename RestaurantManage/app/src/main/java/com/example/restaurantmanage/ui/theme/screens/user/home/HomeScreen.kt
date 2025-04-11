package com.example.restaurantmanage.ui.theme.screens.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.dao.CategoryDao
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.entity.CategoryEntity
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.models.MenuCategory
import com.example.restaurantmanage.data.models.MenuItem
import com.example.restaurantmanage.viewmodels.CartViewModel
import com.example.restaurantmanage.viewmodels.CartViewModelFactory
import com.example.restaurantmanage.viewmodels.HomeViewModel
import com.example.restaurantmanage.viewmodels.HomeViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    ),
    cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    )
) {
    val featuredItems by homeViewModel.featuredItems.collectAsState(initial = emptyList())
    val categories by homeViewModel.categories.collectAsState(initial = emptyList())
    val searchText = remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchText.value,
                        onValueChange = {
                            searchText.value = it
                            homeViewModel.searchMenuItems(it.text)
                        },
                        placeholder = { Text("Tìm kiếm món ăn") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
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
            // Banner
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.featured_banner),
                    contentDescription = "Banner món nổi bật",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Món phổ biến (Featured Items)
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
                            onItemClick = { cartViewModel.addToCart(item) }
                        )
                    }
                }
            }

            // Danh sách danh mục (Categories)
            items(categories) { category ->
                Text(
                    category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.heightIn(max = 1000.dp)
                ) {
                    items(category.items) { item ->
                        MenuItemCard(
                            item = item,
                            onItemClick = { cartViewModel.addToCart(item) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedItemCard(
    item: MenuItem,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(80.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = getImageResId(item.image)),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(
    item: MenuItem,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(id = getImageResId(item.image)),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${item.price.toInt()} VNĐ",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Black
            )
            if (!item.inStock) {
                Text(
                    text = "Hết hàng",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun getImageResId(imageName: String): Int {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.placeholder
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // Dữ liệu mẫu
    val mockFeaturedItems = listOf(
        MenuItem("1", "Tôm sốt cà chua", 60000.0, 1, 10, true, "tom_sot_ca", "Ngon tuyệt"),
        MenuItem("2", "Nước ép dâu", 40000.0, 2, 5, true, "nuoc_ep_dau", "Tươi mát")
    )
    val mockCategories = listOf(
        MenuCategory(1, "Thức ăn", mockFeaturedItems.filter { it.categoryId == 1 }),
        MenuCategory(2, "Đồ uống", mockFeaturedItems.filter { it.categoryId == 2 })
    )

    // Mock HomeViewModel cho Preview
    val mockHomeViewModel = object : HomeViewModel(
        menuItemDao = FakeMenuItemDao(),
        categoryDao = FakeCategoryDao()
    ) {
        override val featuredItems: StateFlow<List<MenuItem>>
            get() = MutableStateFlow(mockFeaturedItems)

        override val categories: StateFlow<List<MenuCategory>>
            get() = MutableStateFlow(mockCategories)

//        override fun searchMenuItems(query: String) {
//            // Không cần thực hiện tìm kiếm trong Preview
//        }
    }

    HomeScreen(
        navController = rememberNavController(),
        homeViewModel = mockHomeViewModel
    )
}

// Fake DAO cho Preview
class FakeMenuItemDao : MenuItemDao {
    override fun getAllMenuItems(): Flow<List<MenuItemEntity>> = flowOf(emptyList())
    override fun getMenuItemsByCategory(categoryId: Int): Flow<List<MenuItemEntity>> = flowOf(emptyList())
    override suspend fun getMenuItemById(id: String): MenuItemEntity? = null
    override suspend fun insertMenuItem(menuItem: MenuItemEntity) {}
    override suspend fun insertMenuItems(menuItems: List<MenuItemEntity>) {}
    override suspend fun updateMenuItem(menuItem: MenuItemEntity) {}
    override suspend fun deleteMenuItem(menuItem: MenuItemEntity) {}
    override fun getAvailableMenuItems(): Flow<List<MenuItemEntity>> = flowOf(emptyList())
}

class FakeCategoryDao : CategoryDao {
    override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(emptyList())
    override suspend fun getCategoryById(id: Int): CategoryEntity? = null
    override suspend fun insertCategory(category: CategoryEntity) {}
    override suspend fun insertCategories(categories: List<CategoryEntity>) {}
    override suspend fun updateCategory(category: CategoryEntity) {}
    override suspend fun deleteCategory(category: CategoryEntity) {}
}