package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun getOrdersForSeller(sellerId: String): Flow<List<Order>>

    suspend fun updateOrderStatus(orderId: String, newStatus: String)
}
