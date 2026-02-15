package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.model.Order

class PlaceOrderUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(order: Order) {
        repository.placeOrder(order)
    }
}
