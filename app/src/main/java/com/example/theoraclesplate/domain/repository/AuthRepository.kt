package com.example.theoraclesplate.domain.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getCurrentUser(): FirebaseUser?

    suspend fun login(email: String, pass: String): Flow<Result<AuthResult>>

    suspend fun signup(email: String, pass: String): Flow<Result<AuthResult>>

    suspend fun logout()
}
