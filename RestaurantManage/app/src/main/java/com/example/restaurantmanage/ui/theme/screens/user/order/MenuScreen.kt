package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.data.models.MenuItem
import com.example.restaurantmanage.data.models.MenuCategory

@Composable
fun MenuItemView(menuItem: MenuItem, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // Navigate to detail screen
                navController.navigate("detail/${menuItem.id}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(menuItem.image),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(text = menuItem.name, fontSize = 18.sp)
        Text(
            text = "${menuItem.price} VND",
            textDecoration = if (menuItem.orderCount > 0) TextDecoration.LineThrough else null,
            fontSize = 16.sp
        )
        if (menuItem.orderCount > 0) {
            Text(text = "${menuItem.price * 0.75} VND", color = Color.Red, fontSize = 16.sp) // Example discount
        }
    }
}

@Composable
fun MenuScreen(navController: NavController, categories: List<MenuCategory>) {
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Column(modifier = Modifier.padding(16.dp)) {
        AppBar("Thực đơn", navController, true)

        // Category selection
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            categories.forEach { category ->
                Button(
                    onClick = { selectedCategory = category },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory.id == category.id) Color.Green else Color.LightGray,
                        contentColor = if (selectedCategory.id == category.id) Color.White else Color.Black
                    )
                ) {
                    Text(category.name, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(selectedCategory.items) { item ->
                MenuItemView(item, navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    val sampleItems = listOf(
        MenuItem(id = "1", name = "Tôm sốt cà chua", price = 60000.0, orderCount = 0, image = "https://example.com/image1.jpg", categoryId = 1),
        MenuItem(id = "2", name = "Nước ép trái cây", price = 40000.0, orderCount = 5, image = "https://example.com/image2.jpg", categoryId = 2)
    )

    val sampleCategories = listOf(
        MenuCategory(id = 1, name = "Thức ăn", items = sampleItems.filter { it.categoryId == 1 }),
        MenuCategory(id = 2, name = "Đồ uống", items = sampleItems.filter { it.categoryId == 2 })
    )

    MenuScreen(navController = rememberNavController(), categories = sampleCategories)
}