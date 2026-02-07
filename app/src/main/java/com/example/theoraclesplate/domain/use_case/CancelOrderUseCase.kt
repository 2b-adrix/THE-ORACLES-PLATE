package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.OrderRepository

class CancelOrderUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(orderId: String) {
        repository.updateOrderStatus(orderId, "Cancelled")
    }
}
