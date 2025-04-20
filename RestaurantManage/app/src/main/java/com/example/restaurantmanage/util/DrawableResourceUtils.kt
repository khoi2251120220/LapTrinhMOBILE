package com.example.restaurantmanage.util

import com.example.restaurantmanage.R

/**
 * Lớp tiện ích để quản lý tài nguyên drawable
 */
object DrawableResourceUtils {

    /**
     * Ánh xạ tên drawable thành ID tài nguyên
     * @param drawableName Tên của drawable
     * @return ID tài nguyên từ R.drawable hoặc null nếu không tìm thấy
     */
    fun getDrawableResourceId(drawableName: String): Int? {
        return drawableMap[drawableName]
    }
    
    /**
     * Danh sách các drawable có sẵn để hiển thị trong dialog
     */
    val availableDrawables = listOf(
        "food1", "food2", "drink", "nuocepdau", "nuoceple", 
        "nuoceptao", "nuocepthom", "placeholder_food", "table_image"
    )
    
    /**
     * Ánh xạ từ tên drawable sang ID tài nguyên
     */
    private val drawableMap = mapOf(
        "food1" to R.drawable.food1,
        "food2" to R.drawable.food2,
        "drink" to R.drawable.drink,
        "nuocepdau" to R.drawable.nuocepdau,
        "nuoceple" to R.drawable.nuoceple,
        "nuoceptao" to R.drawable.nuoceptao,
        "nuocepthom" to R.drawable.nuocepthom,
        "placeholder_food" to R.drawable.placeholder_food,
        "table_image" to R.drawable.table_image,
        "placeholder" to R.drawable.placeholder,
        "restaurant_logo" to R.drawable.restaurant_logo,
        "featured_banner" to R.drawable.featured_banner
    )
} 