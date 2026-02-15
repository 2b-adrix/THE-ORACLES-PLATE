package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private val auth: FirebaseAuth, private val database: FirebaseDatabase) : AuthRepository {

    private val dbRef = database.reference

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun login(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        auth.signInWithEmailAndPassword(email, pass).await()
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
    }.flowOn(Dispatchers.IO)

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
    }.flowOn(Dispatchers.IO)

    override suspend fun createUser(user: User, userId: String) = withContext(Dispatchers.IO) {
        dbRef.child("users").child(userId).setValue(user).await()
    }

    override suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val snapshot = dbRef.child("users").child(userId).child("role").get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            null
        }
    }
}