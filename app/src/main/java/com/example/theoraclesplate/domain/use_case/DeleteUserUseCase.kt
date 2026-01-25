package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow

class DeleteUserUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(userId: String): Flow<Result<Unit>> {
        return repository.deleteUser(userId)
    }
}
