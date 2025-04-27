package com.example.restaurantmanage.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RatingComponent(
    modifier: Modifier = Modifier,
    onRatingChanged: (Int) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        for (i in 1..5) {
            IconButton(onClick = {
                rating = i
                onRatingChanged(i)
            }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .width(40.dp) // Sử dụng width
                        .height(40.dp) // Sử dụng height
                )
            }
        }
    }
}

@Suppress("unused")
@Preview(showBackground = true)
@Composable
fun RatingComponentPreview() {
    RatingComponent(
        onRatingChanged = {

        }
    )
}