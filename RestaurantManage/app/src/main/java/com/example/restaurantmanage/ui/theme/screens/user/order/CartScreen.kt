package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.dao.CartItemDao
import com.example.restaurantmanage.data.local.entity.CartItemEntity
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.models.MenuItem
import com.example.restaurantmanage.viewmodels.CartViewModel
import com.example.restaurantmanage.viewmodels.CartViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    )
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total by cartViewModel.total.collectAsState()
    val tax by cartViewModel.tax.collectAsState()
    val totalWithTax by cartViewModel.totalWithTax.collectAsState()
    val selectedPayment by cartViewModel.selectedPayment.collectAsState()
    val paymentMethods by cartViewModel.paymentMethods.collectAsState()
    val isPaymentDropdownExpanded by cartViewModel.isPaymentDropdownExpanded.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Tổng tiền
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng tiền:", fontSize = 18.sp)
                    Text("${total.toInt()} VNĐ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                // Thuế
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thuế (10%):", fontSize = 18.sp)
                    Text("${tax.toInt()} VNĐ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                // Tổng cộng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng cộng:", fontSize = 18.sp)
                    Text("${totalWithTax.toInt()} VNĐ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Chọn phương thức thanh toán
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { cartViewModel.togglePaymentDropdown() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phương thức thanh toán: $selectedPayment", fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = isPaymentDropdownExpanded,
                    onDismissRequest = { cartViewModel.dismissPaymentDropdown() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                cartViewModel.setPaymentMethod(method)
                                cartViewModel.dismissPaymentDropdown()
                            }
                        )
                    }
                }

                // Nút xác nhận thanh toán
                Button(
                    onClick = {
                        cartViewModel.confirmPayment()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Xác nhận thanh toán", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng trống", fontSize = 20.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemRow(cartItem = cartItem)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemRow(cartItem: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh món ăn
            Image(
                painter = painterResource(id = getImageResId(cartItem.menuItem.image)),
                contentDescription = cartItem.menuItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin món ăn
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Giá: ${cartItem.menuItem.price.toInt()} VNĐ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Số lượng: ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Tổng tiền cho món này
            Text(
                text = "${(cartItem.menuItem.price * cartItem.quantity).toInt()} VNĐ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
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
fun PreviewCartScreen() {
    val mockCartItems = listOf(
        CartItem(MenuItem("1", "Nước ép lê", 50000.0, 1, 0, true, "nuoceple", ""), 2),
        CartItem(MenuItem("2", "Nước ép dâu", 55000.0, 2, 0, true, "nuocepdau", ""), 1)
    )

    val mockCartViewModel = object : CartViewModel(FakeCartItemDao()) {
        override val cartItems: StateFlow<List<CartItem>>
            get() = MutableStateFlow(mockCartItems)

        override val total: StateFlow<Double>
            get() = MutableStateFlow(mockCartItems.sumOf { it.menuItem.price * it.quantity })

        override val tax: StateFlow<Double>
            get() = MutableStateFlow(total.value * 0.1)

        override val totalWithTax: StateFlow<Double>
            get() = MutableStateFlow(total.value * 1.1)

        override val selectedPayment: StateFlow<String>
            get() = MutableStateFlow("Tiền mặt")

        override val paymentMethods: StateFlow<List<String>>
            get() = MutableStateFlow(listOf("Tiền mặt", "Thẻ tín dụng", "Ví điện tử"))

        override val isPaymentDropdownExpanded: StateFlow<Boolean>
            get() = MutableStateFlow(false)

//        override fun confirmPayment() {
//            // Không cần thực hiện trong Preview
//        }
    }

    CartScreen(
        navController = rememberNavController(),
        cartViewModel = mockCartViewModel
    )
}

// Fake DAO cho Preview
class FakeCartItemDao : CartItemDao {
    override fun getAllCartItems(): Flow<List<CartItemEntity>> = flowOf(emptyList())
    override suspend fun insertCartItem(cartItem: CartItemEntity) {}
    override suspend fun updateCartItem(cartItem: CartItemEntity) {}
    override suspend fun deleteCartItem(cartItem: CartItemEntity) {}
    override suspend fun clearCart() {}
}