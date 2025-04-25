package com.example.restaurantmanage.ui.theme.screens.user.order

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.models.MenuItem
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.util.DrawableResourceUtils
import com.example.restaurantmanage.viewmodels.CartViewModel
import com.example.restaurantmanage.viewmodels.CartViewModelFactory
import com.example.restaurantmanage.viewmodels.MenuViewModel
import java.io.File
import androidx.compose.runtime.collectAsState

@Composable
fun FoodDetailScreen(
    menuItemId: String,
    viewModel: MenuViewModel,
    navController: NavController
) {
    var menuItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(RestaurantDatabase.getDatabase(context))
    )
    var quantity by remember { mutableIntStateOf(1) }
    
    // Lấy số lượng món ăn trong giỏ hàng
    val cartItems = cartViewModel.cartItems.collectAsState().value
    val itemCount = cartItems.sumOf { it.quantity }

    LaunchedEffect(menuItemId) {
        viewModel.getMenuItemById(menuItemId).collect { item ->
            menuItem = item
            Log.d("FoodDetailScreen", "Loaded item: ${item?.name}, image: ${item?.image}")
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                AppBar(
                    title = "Chi tiết món ăn",
                    navController = navController,
                    showBackButton = true
                )

                IconButton(
                    onClick = { navController.navigate("cart") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Giỏ hàng",
                            tint = Color.Black
                        )
                        
                        if (itemCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 5.dp, y = (-10).dp)
                                    .size(width = if (itemCount > 9) 24.dp else 20.dp, height = 20.dp)
                                    .background(Color.Black, CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (itemCount > 99) "99+" else itemCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            menuItem?.let {
                // Hiển thị hình ảnh
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                ) {
                    
                    if (it.image.isNotEmpty() && !it.image.startsWith("/")) {
                        // Sử dụng DrawableResourceUtils
                        val resourceId = DrawableResourceUtils.getDrawableResourceId(it.image)
                        
                        if (resourceId != null) {
                            // Nếu là drawable resource
                            Log.d("FoodDetailScreen", "Loading drawable resource: ${it.image}, id: $resourceId")
                            Image(
                                painter = painterResource(id = resourceId),
                                contentDescription = it.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Nếu không tìm thấy drawable, hiển thị ảnh placeholder
                            AsyncImage(
                                model = "https://via.placeholder.com/200",
                                contentDescription = it.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else if (it.image.startsWith("/")) {
                        // Nếu là đường dẫn file
                        val file = File(it.image)
                        Log.d("FoodDetailScreen", "Loading image from file: ${it.image}, exists: ${file.exists()}")
                        AsyncImage(
                            model = if (file.exists()) it.image else "https://via.placeholder.com/200",
                            contentDescription = it.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Nếu không có ảnh
                        AsyncImage(
                            model = "https://via.placeholder.com/200",
                            contentDescription = it.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = it.name,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "${it.price.toInt()} VNĐ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Mô tả",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = it.description,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Thêm phần chọn số lượng
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Số lượng:",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-", fontSize = 20.sp)
                        }
                        
                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 18.sp
                        )
                        
                        Button(
                            onClick = { quantity++ },
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+", fontSize = 20.sp)
                        }
                    }

                    Button(
                        onClick = {
                            // Chuyển đổi từ MenuItemEntity sang MenuItem
                            val menuItemModel = MenuItem(
                                id = it.id,
                                name = it.name,
                                price = it.price,
                                categoryId = it.categoryId,
                                orderCount = 0,
                                inStock = true,
                                image = it.image,
                                description = it.description,
                                imageResId = 0
                            )
                            
                            // Thêm item vào giỏ hàng với số lượng đã chọn
                            for (i in 1..quantity) {
                                cartViewModel.addToCart(menuItemModel)
                            }
                            
                            // Hiển thị thông báo thành công thay vì chuyển đến giỏ hàng
                            quantity = 1 // Reset số lượng về 1 sau khi thêm vào giỏ
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Thêm vào giỏ hàng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } ?: run {
                Text(text = "Loading...", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
