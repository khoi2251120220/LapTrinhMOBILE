package com.example.restaurantmanage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.restaurantmanage.ui.theme.screens.admin.MainScreenAdmin
import com.example.restaurantmanage.ui.theme.screens.assignment.AssignmentMain
import com.example.restaurantmanage.ui.theme.screens.user.MainScreenUser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           // AssignmentMain()
           // MainScreenUser()
           // MainScreenAdmin()
        }
    }
}