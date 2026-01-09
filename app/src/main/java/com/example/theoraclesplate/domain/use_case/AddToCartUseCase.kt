package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.model.CartItem

class AddToCartUseCase(private val repository: CartRepository) {

    suspend operator fun invoke(userId: String, cartItem: CartItem) {
        repository.addToCart(userId, cartItem)
    }
}
