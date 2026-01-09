package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository

class RemoveFromCartUseCase(private val repository: CartRepository) {

    suspend operator fun invoke(userId: String, foodItemId: String) {
        repository.removeFromCart(userId, foodItemId)
    }
}
