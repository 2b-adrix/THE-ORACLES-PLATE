package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

class GetOrdersForSellerUseCase(private val repository: OrderRepository) {

    operator fun invoke(sellerId: String): Flow<List<Order>> {
        return repository.getOrdersForSeller(sellerId)
    }
}
