package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository

class UpdateQuantityUseCase(private val repository: CartRepository) {

    suspend operator fun invoke(userId: String, foodItemId: String, newQuantity: Int) {
        repository.updateQuantity(userId, foodItemId, newQuantity)
    }
}
