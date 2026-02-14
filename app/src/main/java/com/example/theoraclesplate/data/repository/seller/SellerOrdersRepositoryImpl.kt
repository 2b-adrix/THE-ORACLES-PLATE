package com.example.theoraclesplate.data.repository.seller

import com.example.theoraclesplate.domain.repository.seller.SellerOrdersRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SellerOrdersRepositoryImpl : SellerOrdersRepository {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    override fun getOrders(): Flow<List<Order>> = callbackFlow {
        val sellerId = auth.currentUser?.uid ?: run {
            close(Exception("User not logged in"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                    .filter { order -> order.items.any { it.sellerId == sellerId } }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child("orders").addValueEventListener(listener)

        awaitClose { database.child("orders").removeEventListener(listener) }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        database.child("orders").child(orderId).child("status").setValue(newStatus)
    }
}
