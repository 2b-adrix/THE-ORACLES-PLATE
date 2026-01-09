package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.model.User

class CreateUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(user: User, userId: String) {
        repository.createUser(user, userId)
    }
}
