package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getCartItems(userId: String): Flow<List<CartItem>>

    suspend fun addToCart(userId: String, cartItem: CartItem): Void?

    suspend fun removeFromCart(userId: String, cartItemId: String): Void?

    suspend fun updateQuantity(userId: String, cartItemId: String, newQuantity: Int): Void?

    suspend fun getCartItem(userId: String, foodItemId: String): CartItem?

    suspend fun clearCart(userId: String): Void?
}
