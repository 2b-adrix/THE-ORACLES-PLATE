package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.CheckoutRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CheckoutRepositoryImpl : CheckoutRepository {

    private val database = Firebase.database.reference

    override suspend fun createOrder(order: Order) {
        database.child("orders").child(order.orderId).setValue(order).await()
        database.child("users").child(order.userId).child("order_history").child(order.orderId).setValue(order).await()
        database.child("users").child(order.userId).child("cart").removeValue().await()
    }
}
