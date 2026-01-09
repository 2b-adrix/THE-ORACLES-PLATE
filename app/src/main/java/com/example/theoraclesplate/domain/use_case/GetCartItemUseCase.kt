package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.model.CartItem

class GetCartItemUseCase(private val repository: CartRepository) {

    suspend operator fun invoke(userId: String, foodItemId: String): CartItem? {
        return repository.getCartItem(userId, foodItemId)
    }
}
