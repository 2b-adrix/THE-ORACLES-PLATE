package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun login(email: String, pass: String): Flow<Result<AuthResult>> = flow {
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun signup(email: String, pass: String, name: String, role: String): Flow<Result<AuthResult>> = flow {
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = User(name = name, email = email, role = role) // Create user with specified role
            result.user?.uid?.let { createUser(user, it) } // Store user in database
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>> = flow {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun createUser(user: User, userId: String) {
        database.child("users").child(userId).setValue(user).await()
    }

    override suspend fun getUserRole(userId: String): String? {
        return try {
            val snapshot = database.child("users").child(userId).child("role").get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
