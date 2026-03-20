package com.bit.bithub.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

// Вспомогательная функция для конвертации Hex в Color
fun String?.toColor(): Color {
    if (this == null) return Color(0xFF2C6CFF)
    return try {
        Color(this.toColorInt())
    } catch (_: Exception) {
        Color(0xFF2C6CFF) // Запасной синий цвет
    }
}
