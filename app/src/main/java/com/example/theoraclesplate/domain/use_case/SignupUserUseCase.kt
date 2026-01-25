package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

class SignupUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, pass: String, name: String, role: String): Flow<Result<AuthResult>> {
        return repository.signup(email, pass, name, role)
    }
}
