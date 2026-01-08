package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository

class LogoutUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke() {
        repository.logout()
    }
}
