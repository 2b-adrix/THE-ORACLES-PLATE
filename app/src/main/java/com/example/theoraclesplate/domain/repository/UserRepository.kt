package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
}
