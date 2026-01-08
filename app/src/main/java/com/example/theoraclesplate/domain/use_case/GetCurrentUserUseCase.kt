package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase(private val repository: AuthRepository) {

    operator fun invoke(): FirebaseUser? {
        return repository.getCurrentUser()
    }
}
