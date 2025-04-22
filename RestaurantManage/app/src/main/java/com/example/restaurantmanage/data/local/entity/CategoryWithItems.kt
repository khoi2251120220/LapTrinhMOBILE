package com.example.restaurantmanage.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithItems(
    val id: Int,
    val name: String,
    val description: String = "",
    val items: List<MenuItemEntity> = emptyList()
)

/*
 * This is the class that would be used with proper Room relationships:
 * 
 * data class CategoryWithItems(
 *     @Embedded val category: CategoryEntity,
 *     @Relation(
 *         parentColumn = "id",
 *         entityColumn = "category_id"
 *     )
 *     val items: List<MenuItemEntity>
 * )
 */ 