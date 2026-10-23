package com.example.minesweeper

enum class Level(
    val title: String,
    val size: Int,
    val mineCount: Int
) {
    EASY("Лёгкий", 8, 10),
    MEDIUM("Средний", 10, 18),
    HARD("Сложный", 12, 30)
}