package com.example.restaurantmanage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.CategoryEntity
import com.example.restaurantmanage.ui.theme.screens.admin.MainScreenAdmin
import com.example.restaurantmanage.ui.theme.screens.assignment.AssignmentMain
import com.example.restaurantmanage.ui.theme.screens.user.MainScreenUser

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


             MainScreenUser()
           // MainScreenAdmin()
          // AssignmentMain()


        }
    }
}

