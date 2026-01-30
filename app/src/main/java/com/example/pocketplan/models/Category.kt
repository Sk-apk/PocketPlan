package com.pocketplan.models

data class Category(
    val categoryId: Int = 0,
    val categoryName: String,
    val categoryColor: String,
    val categoryIcon: String,
    val userId: Int,
    val createdAt: String = ""
)