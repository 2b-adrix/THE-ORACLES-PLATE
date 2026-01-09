package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.OrderRepository

class UpdateOrderStatusUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String, newStatus: String) = repository.updateOrderStatus(orderId, newStatus)
}
