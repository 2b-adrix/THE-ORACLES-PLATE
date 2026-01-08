package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.Order
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {

    fun getReadyForPickupOrders(): Flow<List<Order>>

    fun getOutForDeliveryOrders(): Flow<List<Order>>

    fun getDeliveredOrders(): Flow<List<Order>>
}
