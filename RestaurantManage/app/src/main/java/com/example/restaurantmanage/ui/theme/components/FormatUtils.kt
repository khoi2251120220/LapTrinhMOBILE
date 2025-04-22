package com.example.restaurantmanage.ui.theme.components

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a Double value as Vietnamese currency
 * @param amount The amount to format
 * @return Formatted currency string in VND
 */
fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        .format(amount)
        .replace("₫", "VNĐ")
} 