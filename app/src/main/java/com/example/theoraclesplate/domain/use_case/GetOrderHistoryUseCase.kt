package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.HistoryRepository
import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

class GetOrderHistoryUseCase(private val repository: HistoryRepository) {

    operator fun invoke(userId: String): Flow<List<Order>> {
        return repository.getOrderHistory(userId)
    }
}
