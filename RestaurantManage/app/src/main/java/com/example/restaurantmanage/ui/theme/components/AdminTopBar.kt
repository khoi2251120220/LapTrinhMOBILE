package com.example.restaurantmanage.ui.theme.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantmanage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppBar(
    title: String,
    navController: NavController,
    onMenuClick: () -> Unit = {}, // Callback khi nhấn icon menu
    onAvatarClick: () -> Unit = {} // Callback khi nhấn icon avatar
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(onClick = onAvatarClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
@Preview(showBackground = true)
@Composable
fun AdminAppBarPreview() {
    val navController = NavController(androidx.compose.ui.platform.LocalContext.current)
    AdminAppBar(
        title = "QUẢN LÝ DOANH THU",
        navController = navController
    )
}