package com.example.theoraclesplate.model

data class FoodItem(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val sellerId: String = ""
)
