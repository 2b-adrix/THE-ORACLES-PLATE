package com.example.theoraclesplate.model

data class User(
    val name: String = "",
    val email: String = "",
    val role: String = "buyer", // buyer, seller, admin, driver
    val profileImage: String = "", // Added field for Cloudinary URL
    val status: String = "approved" // pending, approved
)
