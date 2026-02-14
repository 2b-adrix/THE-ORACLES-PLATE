package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getCurrentUser(): FirebaseUser?

    suspend fun login(email: String, pass: String): AuthResult

    suspend fun signup(email: String, pass: String, name: String, role: String): Flow<Result<AuthResult>> // Added role

    suspend fun logout()

    suspend fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>

    suspend fun createUser(user: User, userId: String)

    suspend fun getUserRole(userId: String): String?
}
