package com.example.theoraclesplate.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val address: String = "",
    val paymentMethod: String = "COD", // COD, Credit Card
    val status: String = "Pending", // Pending, Preparing, Ready, Out for Delivery, Delivered
    val timestamp: Long = 0,
    val deliveryPersonId: String? = null
)
