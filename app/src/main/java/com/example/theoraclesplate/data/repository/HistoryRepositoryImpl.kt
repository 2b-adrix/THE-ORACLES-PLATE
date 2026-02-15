package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.HistoryRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : HistoryRepository {

    private val ordersRef = database.reference.child("orders")

    override fun getOrderHistory(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                    .filter { it.userId == userId }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)
}
