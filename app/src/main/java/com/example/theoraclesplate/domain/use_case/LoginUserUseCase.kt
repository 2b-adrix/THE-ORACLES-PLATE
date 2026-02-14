package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult

class LoginUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, pass: String): AuthResult {
        return repository.login(email, pass)
    }
}
