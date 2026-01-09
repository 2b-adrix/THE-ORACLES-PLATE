package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getCartItems(userId: String): Flow<List<CartItem>>

    suspend fun addToCart(userId: String, cartItem: CartItem)

    suspend fun removeFromCart(userId: String, cartItemId: String)

    suspend fun updateQuantity(userId: String, cartItemId: String, newQuantity: Int)

    suspend fun getCartItem(userId: String, foodItemId: String): CartItem?
}
