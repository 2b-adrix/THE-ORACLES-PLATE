package com.example.theoraclesplate.model

data class OrderItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 1,
    val sellerId: String = ""
)
