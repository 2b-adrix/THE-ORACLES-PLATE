package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.HistoryRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HistoryRepositoryImpl : HistoryRepository {

    private val database = Firebase.database.reference

    override fun getOrderHistory(userId: String): Flow<List<Order>> = callbackFlow {
        val historyRef = database.child("users").child(userId).child("order_history")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                trySend(orders.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        historyRef.addValueEventListener(listener)
        awaitClose { historyRef.removeEventListener(listener) }
    }
}
