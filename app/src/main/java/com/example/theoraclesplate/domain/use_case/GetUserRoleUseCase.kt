package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository

class GetUserRoleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String): String? = repository.getUserRole(userId)
}
