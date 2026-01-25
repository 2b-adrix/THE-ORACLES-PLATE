package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.User
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    fun getPendingSellers(): Flow<Result<List<Pair<String, User>>>>

    suspend fun approveSeller(userId: String): Flow<Result<Unit>>

    suspend fun declineSeller(userId: String): Flow<Result<Unit>>

    fun getAllUsers(): Flow<Result<List<Pair<String, User>>>>

    fun getAllOrders(): Flow<Result<List<Order>>>

    fun getAnalyticsData(): Flow<Result<Map<String, Any>>>

    fun getDeliveryUsers(): Flow<Result<List<Pair<String, User>>>>
    
    fun getAllMenuItems(): Flow<Result<List<FoodItem>>>

    suspend fun deleteOrder(orderId: String): Flow<Result<Unit>>

    suspend fun deleteUser(userId: String): Flow<Result<Unit>>
}
