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

data class FoodItem(
    val imageUrl: String,
    val title: String,
    val originalPrice: String,
    val discountedPrice: String
)

data class DrinkItem(
    val imageUrl: String,
    val title: String,
    val originalPrice: String,
    val discountedPrice: String
)

//View hiển thị thức ăn
@Composable
fun FoodItemView(foodItem: FoodItem, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // xử lý sự kiện click để đến trang detaildetail
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(foodItem.imageUrl),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(text = foodItem.title)
        Text(
            text = foodItem.originalPrice,
            textDecoration = TextDecoration.LineThrough
        )
        Text(text = foodItem.discountedPrice, color = Color.Red)
    }
}

//View hiển thị đồ uống
@Composable
fun DrinkItemView(drinkItem: DrinkItem, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // xử lý sự kiện click để đến trang detaildetail
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(drinkItem.imageUrl),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(text = drinkItem.title)
        Text(
            text = drinkItem.originalPrice,
            textDecoration = TextDecoration.LineThrough
        )
        Text(text = drinkItem.discountedPrice, color = Color.Red)
    }
}

//Màn hình
@Composable
fun MenuScreen(navController: NavController) {
    val isFoodSelected = remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        AppBar("Thực đơn", navController, true)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = { isFoodSelected.value = true },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFoodSelected.value) Color.Green else Color.LightGray,
                    contentColor = if (isFoodSelected.value) Color.White else Color.Black
                )
            ) {
                Text("Thức ăn", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(3.dp))

            Button(
                onClick = { isFoodSelected.value = false },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isFoodSelected.value) Color.Green else Color.LightGray,
                    contentColor = if (!isFoodSelected.value) Color.White else Color.Black
                )
            ) {
                Text("Đồ uống", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            val itemsToShow = if (isFoodSelected.value) foodItems else drinkItems
            items(itemsToShow.chunked(2)) { itemPair ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    itemPair.forEach { item ->
                        when (item) {
                            is FoodItem -> FoodItemView(item, navController)
                            is DrinkItem -> DrinkItemView(item, navController)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}


val foodItems = listOf(
    //Test, ch biết thêm ảnh sao :)
    FoodItem(
        imageUrl = "",
        title = "Tôm sốt cà chua",
        originalPrice = "80 000 VND",
        discountedPrice = "60 000 VND"
    ),
    FoodItem(
        imageUrl = "",
        title = "Tôm sốt cà chua",
        originalPrice = "80 000 VND",
        discountedPrice = "60 000 VND"
    ),
    FoodItem(
        imageUrl = "",
        title = "Tôm sốt cà chua",
        originalPrice = "80 000 VND",
        discountedPrice = "60 000 VND"
    ), FoodItem(
        imageUrl = "",
        title = "Tôm sốt cà chua",
        originalPrice = "80 000 VND",
        discountedPrice = "60 000 VND"
    )
)

val drinkItems = listOf(
    DrinkItem(
        imageUrl = "",
        title = "Nước ép trái cây",
        originalPrice = "50 000 VND",
        discountedPrice = "40 000 VND"
    )
)

@Preview(showBackground = true)
@Composable
fun MenuReview() {
    MenuScreen(navController = rememberNavController())
}