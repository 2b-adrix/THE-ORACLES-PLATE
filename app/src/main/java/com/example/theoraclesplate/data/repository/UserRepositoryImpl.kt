package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.UserRepository
import com.example.theoraclesplate.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override fun getAllUsers(): Flow<List<User>> = flow {
        val snapshot = firestore.collection("users").get().await()
        val users = snapshot.documents.mapNotNull { document ->
            val user = document.toObject(User::class.java)
            user?.uid = document.id
            user
        }
        emit(users)
    }.flowOn(Dispatchers.IO)
}
