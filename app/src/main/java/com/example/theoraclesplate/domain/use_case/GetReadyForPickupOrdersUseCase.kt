package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

class GetReadyForPickupOrdersUseCase(private val repository: DeliveryRepository) {

    operator fun invoke(): Flow<List<Order>> {
        return repository.getReadyForPickupOrders()
    }
}
