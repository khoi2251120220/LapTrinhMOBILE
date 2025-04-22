package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.ui.theme.components.AppBar
import com.example.restaurantmanage.util.DrawableResourceUtils
import com.example.restaurantmanage.viewmodels.CartViewModel
import com.example.restaurantmanage.viewmodels.CartViewModelFactory
import com.example.restaurantmanage.viewmodels.OrderViewModel
import com.example.restaurantmanage.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(RestaurantDatabase.getDatabase(LocalContext.current))
    ),
    orderViewModel: OrderViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total by cartViewModel.total.collectAsState()
    val tax by cartViewModel.tax.collectAsState()
    val totalWithTax by cartViewModel.totalWithTax.collectAsState()
    val selectedPayment by cartViewModel.selectedPayment.collectAsState()
    val paymentMethods by cartViewModel.paymentMethods.collectAsState()
    val isPaymentDropdownExpanded by cartViewModel.isPaymentDropdownExpanded.collectAsState()
    
    // Lấy thông tin người dùng từ ProfileViewModel
    val userProfile by profileViewModel.userProfile.collectAsState()
    
    // Coroutine scope
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppBar(
                title = "THANH TOÁN",
                navController = navController,
                showBackButton = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartView(navController)
            } else {
                // Payment method selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thanh toán bằng",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { cartViewModel.togglePaymentDropdown() }
                        ) {
                            Text(
                                text = selectedPayment,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Select",
                                tint = Color.Gray
                            )
                        }
                    }
                }
                
                // Discount code section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mã giảm giá",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sử dụng mã khuyến mãi",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Enter code",
                                tint = Color.Gray
                            )
                        }
                    }
                }
                
                // Section title for items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MÓN ĂN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "MÔ TẢ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "GIÁ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.End
                    )
                }
                
                // Cart items
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(cartItems) { cartItem ->
                        PaymentCartItemRow(cartItem = cartItem)
                    }
                }
                
                // Order summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng cộng (${cartItems.size})",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "${total.toInt()} VNĐ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Thuế",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "${tax.toInt()} VNĐ",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "${totalWithTax.toInt()} VNĐ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
                
                // Checkout button
                Button(
                    onClick = {
                        // Xử lý thanh toán
                        scope.launch {
                            try {
                                // Lấy tên người dùng từ Profile, nếu không có thì dùng "Khách hàng"
                                val customerName = userProfile.name.ifBlank { "Khách hàng" }
                                
                                // Tạo đơn hàng từ giỏ hàng với tên người dùng thật
                                val orderId = orderViewModel.createOrderFromCart(
                                    cartItems = cartItems.map { cartItem ->
                                        com.example.restaurantmanage.data.local.entity.CartItemEntity(
                                            menuItemId = cartItem.menuItem.id,
                                            userId = FirebaseAuth.getInstance().currentUser?.uid,
                                            quantity = cartItem.quantity,
                                            price = cartItem.menuItem.price,
                                            notes = cartItem.notes ?: ""
                                        )
                                    },
                                    totalAmount = totalWithTax,
                                    customerName = customerName
                                )
                                
                                // Xóa giỏ hàng
                                cartViewModel.confirmPayment()
                                
                                // Chuyển đến màn hình thanh toán thành công
                                navController.navigate("payment_success/$orderId/$customerName/${totalWithTax.toInt()}")
                            } catch (e: Exception) {
                                // Xử lý lỗi nếu có
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "XÁC NHẬN THANH TOÁN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Dropdown for payment methods if expanded
                DropdownMenu(
                    expanded = isPaymentDropdownExpanded,
                    onDismissRequest = { cartViewModel.dismissPaymentDropdown() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .background(Color.White)
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
            }
        }
    }
}

@Composable
fun EmptyCartView(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder), // Thay bằng hình ảnh phù hợp
                contentDescription = "Empty Cart",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )
            
            Text(
                text = "Giỏ hàng của bạn đang trống",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Hãy thêm món ăn vào giỏ hàng trước khi thanh toán",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Button(
                onClick = { navController.navigate("menu") },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Xem thực đơn",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PaymentCartItemRow(cartItem: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = DrawableResourceUtils.getDrawableResourceId(cartItem.menuItem.image) ?: R.drawable.placeholder
                ),
                contentDescription = cartItem.menuItem.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Thông tin món ăn
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.menuItem.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Mô tả",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Số Lượng: ${cartItem.quantity}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Giá
            Text(
                text = "${(cartItem.menuItem.price).toInt()} VNĐ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

