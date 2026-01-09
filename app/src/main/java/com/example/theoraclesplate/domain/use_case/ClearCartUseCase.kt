package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(userId: String) = repository.clearCart(userId)
}
