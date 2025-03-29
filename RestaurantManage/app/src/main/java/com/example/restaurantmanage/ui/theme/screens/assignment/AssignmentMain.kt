package com.example.restaurantmanage.ui.theme.screens.assignment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.restaurantmanage.ui.theme.RestaurantManageTheme

@Composable
fun AssignmentMain() {
    RestaurantManageTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AssignmentNavigation()
        }
    }
} 