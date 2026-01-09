package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

class LoginWithGoogleUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(idToken: String): Flow<Result<AuthResult>> {
        return repository.loginWithGoogle(idToken)
    }
}
