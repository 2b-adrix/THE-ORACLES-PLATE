package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.DeliveryRepository
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

class DeliveryRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : DeliveryRepository {

    private val ordersRef = database.reference.child("orders")

    private fun getOrdersByStatus(status: String): Flow<List<Order>> = callbackFlow {
        val query = ordersRef.orderByChild("status").equalTo(status)
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

    override fun getReadyForPickupOrders(): Flow<List<Order>> = getOrdersByStatus("Ready")

    override fun getOutForDeliveryOrders(): Flow<List<Order>> = getOrdersByStatus("Out for Delivery")

    override fun getDeliveredOrders(): Flow<List<Order>> = getOrdersByStatus("Delivered")

    override suspend fun acceptOrder(order: Order) {
        withContext(Dispatchers.IO) {
            ordersRef.child(order.orderId).setValue(order).await()
        }
    }
}
