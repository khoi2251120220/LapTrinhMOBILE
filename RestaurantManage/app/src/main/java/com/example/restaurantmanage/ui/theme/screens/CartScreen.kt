package com.example.restaurantmanage.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.viewmodels.CartViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItem.collectAsState()
    val total by cartViewModel.total.collectAsState()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Thanh toán bằng")
                Text("Tiền mặt")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mã giảm giá")
                Text("Sử dụng mã khuyến mãi")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
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
                    Text(String.format(Locale.US, "%,.0fđ", total * 0.1))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng", fontWeight = FontWeight.Bold)
                    Text(
                        String.format(Locale.US, "%,.0fđ", total * 1.1),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { /* Handle payment */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
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
            painter = painterResource(id = R.drawable.placeholder),
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
@Preview(showBackground = true)
@Composable
fun PreviewCartScreen() {
    CartScreen(navController = NavController(LocalContext.current))
}