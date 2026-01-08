package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.AnalyticsData
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.User
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    fun getPendingSellers(): Flow<List<Pair<String, User>>>

    suspend fun approveSeller(sellerId: String)

    fun getAllUsers(): Flow<List<Pair<String, User>>>

    suspend fun deleteUser(userId: String)

    fun getAllOrders(): Flow<List<Pair<String, Order>>>

    suspend fun deleteOrder(orderId: String)

    fun getAllMenuItems(): Flow<List<Pair<String, FoodItem>>>

    suspend fun deleteMenuItem(menuItemId: String)

    fun getDeliveryUsers(): Flow<List<Pair<String, User>>>

    fun getAnalyticsData(): Flow<AnalyticsData>
}
