package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.CheckoutRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CheckoutRepositoryImpl @Inject constructor(private val database: FirebaseDatabase): CheckoutRepository {

    override suspend fun createOrder(order: Order) {
        database.reference.child("orders").child(order.orderId).setValue(order).await()
        database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).setValue(order).await()
        database.reference.child("users").child(order.userId).child("cart").removeValue().await()
    }
}
