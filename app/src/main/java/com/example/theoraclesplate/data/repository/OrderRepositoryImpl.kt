package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {

    private val database = Firebase.database.reference

    override fun getOrdersForSeller(sellerId: String): Flow<List<Order>> = callbackFlow {
        val ordersRef = database.child("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    val order = it.getValue(Order::class.java)
                    if (order != null) {
                        val sellerItems = order.items.filter { item -> item.sellerId == sellerId }
                        if (sellerItems.isNotEmpty()) {
                            order.copy(items = sellerItems)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        database.child("orders").child(orderId).child("status").setValue(newStatus).await()
        // Also update the order status in the user's order history
        val order = database.child("orders").child(orderId).get().await().getValue(Order::class.java)
        if (order != null) {
            database.child("users").child(order.userId).child("order_history").child(orderId).child("status").setValue(newStatus).await()
        }
    }
}
