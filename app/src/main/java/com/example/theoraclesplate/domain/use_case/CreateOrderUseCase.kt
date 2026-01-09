package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.CheckoutRepository
import com.example.theoraclesplate.model.Order

class CreateOrderUseCase(private val repository: CheckoutRepository) {

    suspend operator fun invoke(order: Order) {
        repository.createOrder(order)
    }
}
