package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow

class DeleteOrderUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(orderId: String): Flow<Result<Unit>> {
        return repository.deleteOrder(orderId)
    }
}
