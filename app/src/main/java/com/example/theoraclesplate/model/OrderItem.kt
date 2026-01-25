package com.example.theoraclesplate.model

data class OrderItem(
    val name: String = "",
    val price: Double = 0.0,
    val image: String = "",
    val quantity: Int = 1,
    val sellerId: String = ""
)
