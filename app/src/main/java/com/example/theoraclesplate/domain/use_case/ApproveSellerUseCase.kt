package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository

class ApproveSellerUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(sellerId: String) {
        repository.approveSeller(sellerId)
    }
}
