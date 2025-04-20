package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "menu_items",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    @ColumnInfo(name = "category_id", index = true) val categoryId: Int, // Foreign key
    @ColumnInfo(name = "order_count", defaultValue = "0") val orderCount: Int,
    @ColumnInfo(name = "in_stock", defaultValue = "1") val inStock: Boolean,
    val image: String,
    val description: String
)

val menuItems = listOf(
    MenuItemEntity(id = "1", name = "Tôm xào chua ngọt", price = 150000.0, categoryId = 1, orderCount = 320, inStock = true, image = "food1", description = "Tôm xào với nước sốt chua ngọt."),
    MenuItemEntity(id = "2", name = "Cá hồi nướng", price = 180000.0, categoryId = 1, orderCount = 98, inStock = true, image = "food2", description = "Cá hồi nướng tươi ngon."),
    MenuItemEntity(id = "3", name = "Bò xào nấm", price = 160000.0, categoryId = 1, orderCount = 75, inStock = true, image = "placeholder_food", description = "Bò xào với nấm và rau củ."),
    MenuItemEntity(id = "4", name = "Bánh flan", price = 25000.0, categoryId = 1, orderCount = 150, inStock = true, image = "food1", description = "Bánh flan mềm mịn, thơm ngon."),
    MenuItemEntity(id = "5", name = "Trà đào", price = 35000.0, categoryId = 2, orderCount = 200, inStock = true, image = "drink", description = "Trà đào thơm mát, giải khát mùa hè."),
    MenuItemEntity(id = "6", name = "Nước ép dâu", price = 30000.0, categoryId = 2, orderCount = 180, inStock = true, image = "nuocepdau", description = "Nước ép dâu tươi ngon."),
    MenuItemEntity(id = "7", name = "Nước ép táo", price = 28000.0, categoryId = 2, orderCount = 160, inStock = true, image = "nuoceptao", description = "Nước ép táo tươi ngon."),
    MenuItemEntity(id = "8", name = "Nước ép thơm", price = 32000.0, categoryId = 2, orderCount = 140, inStock = true, image = "nuocepthom", description = "Nước ép thơm tươi ngon.")
)