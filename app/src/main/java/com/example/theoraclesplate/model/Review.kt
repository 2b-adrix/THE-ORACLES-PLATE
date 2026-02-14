package com.example.theoraclesplate.model

data class Review(
    val rating: Float = 0f,
    val comment: String = "",
    val userId: String = "",
    val userName: String = "",
    val timestamp: Long = 0
)
