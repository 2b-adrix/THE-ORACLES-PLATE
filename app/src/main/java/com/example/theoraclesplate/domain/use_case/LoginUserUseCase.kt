package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

class LoginUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, pass: String): Flow<Result<AuthResult>> {
        return repository.login(email, pass)
    }
}
