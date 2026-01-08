package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository

class DeleteUserUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(userId: String) {
        repository.deleteUser(userId)
    }
}
