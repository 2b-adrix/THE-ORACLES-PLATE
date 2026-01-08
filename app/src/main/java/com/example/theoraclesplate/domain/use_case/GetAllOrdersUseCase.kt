package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

class GetAllOrdersUseCase(private val repository: AdminRepository) {

    operator fun invoke(): Flow<List<Pair<String, Order>>> {
        return repository.getAllOrders()
    }
}
