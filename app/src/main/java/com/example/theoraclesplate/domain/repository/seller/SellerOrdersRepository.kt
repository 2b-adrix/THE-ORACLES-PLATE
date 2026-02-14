package com.example.theoraclesplate.domain.repository.seller

import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

interface SellerOrdersRepository {
    fun getOrders(): Flow<List<Order>>
    suspend fun updateOrderStatus(orderId: String, newStatus: String)
}
