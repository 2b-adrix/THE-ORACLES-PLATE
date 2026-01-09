package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.Order

interface CheckoutRepository {

    suspend fun createOrder(order: Order)
}
