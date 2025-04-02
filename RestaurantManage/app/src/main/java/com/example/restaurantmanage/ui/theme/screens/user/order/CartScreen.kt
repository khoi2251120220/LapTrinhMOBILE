package com.example.restaurantmanage.ui.theme.screens.user.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.viewmodels.CartViewModel
import com.example.restaurantmanage.navigation.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total by cartViewModel.total.collectAsState()
    val totalWithTax by cartViewModel.totalWithTax.collectAsState()
    val tax by cartViewModel.tax.collectAsState()
    val selectedPayment by cartViewModel.selectedPayment.collectAsState()
    val paymentMethods by cartViewModel.paymentMethods.collectAsState()
    val expanded by cartViewModel.isPaymentDropdownExpanded.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("THANH TOÁN") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { cartViewModel.togglePaymentDropdown() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thanh toán bằng")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedPayment)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { cartViewModel.dismissPaymentDropdown() }
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Logic mã giảm giá nếu cần */ },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mã giảm giá")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sử dụng mã khuyến mãi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    CartItemRow(item)
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng cộng (${cartItems.size})")
                    Text(String.format(Locale.US, "%,.0fđ", total))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thuế")
                    Text(String.format(Locale.US, "%,.0fđ", tax))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng", fontWeight = FontWeight.Bold)
                    Text(
                        String.format(Locale.US, "%,.0fđ", totalWithTax),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        cartViewModel.confirmPayment()
                        navController.navigate(Screen.PaymentSuccess.route) // Điều hướng đến PaymentSuccessScreen
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("XÁC NHẬN THANH TOÁN")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = getImageResId(item.menuItem.image)),
            contentDescription = item.menuItem.name,
            modifier = Modifier.size(60.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(item.menuItem.name)
            Text("Số lượng: ${item.quantity}")
        }
        Text(String.format(Locale.US, "%,.0fđ", item.menuItem.price * item.quantity))
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
    CartScreen(navController = rememberNavController())
}