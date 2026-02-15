package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.model.Order

class AcceptOrderUseCase(private val repository: DeliveryRepository) {

    suspend operator fun invoke(order: Order) {
        repository.acceptOrder(order)
    }
}
