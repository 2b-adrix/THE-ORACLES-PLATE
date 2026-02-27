package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : OrderRepository {

    private val ordersRef = database.reference.child("orders")

    override fun getOrdersForSeller(sellerId: String): Flow<List<Order>> = callbackFlow {
        val query = ordersRef.orderByChild("items/0/sellerId").equalTo(sellerId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(Dispatchers.IO) {
        ordersRef.child(orderId).child("status").setValue(newStatus).await()
    }

    override suspend fun placeOrder(order: Order) = withContext(Dispatchers.IO) {
        ordersRef.child(order.orderId).setValue(order).await()
    }
}
