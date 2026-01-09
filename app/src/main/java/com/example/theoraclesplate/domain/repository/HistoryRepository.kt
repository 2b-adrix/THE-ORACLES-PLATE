package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getOrderHistory(userId: String): Flow<List<Order>>
}
