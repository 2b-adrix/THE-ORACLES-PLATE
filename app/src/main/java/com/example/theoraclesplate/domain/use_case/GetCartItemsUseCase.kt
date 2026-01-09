package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.model.CartItem
import kotlinx.coroutines.flow.Flow

class GetCartItemsUseCase(private val repository: CartRepository) {

    operator fun invoke(userId: String): Flow<List<CartItem>> {
        return repository.getCartItems(userId)
    }
}
