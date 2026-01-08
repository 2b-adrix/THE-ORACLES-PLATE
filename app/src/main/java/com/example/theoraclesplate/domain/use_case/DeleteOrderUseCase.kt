package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository

class DeleteOrderUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(orderId: String) {
        repository.deleteOrder(orderId)
    }
}
