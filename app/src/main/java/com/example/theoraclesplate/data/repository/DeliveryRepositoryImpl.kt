package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DeliveryRepositoryImpl : DeliveryRepository {

    private val database = Firebase.database.reference

    override suspend fun acceptOrder(order: Order) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user to accept the order")

        val orderUpdates = mapOf(
            "deliveryPersonId" to userId,
            "status" to "Out for Delivery"
        )
        suspendCancellableCoroutine<Unit> { continuation ->
            database.child("orders").child(order.orderId).updateChildren(orderUpdates)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override fun getReadyForPickupOrders(): Flow<List<Order>> = callbackFlow {
        val query = database.child("orders").orderByChild("status").equalTo("Ready")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull {
                    it.getValue(Order::class.java)
                }.filter { it.deliveryPersonId == null } // Only show orders that haven't been accepted
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    override fun getOutForDeliveryOrders(): Flow<List<Order>> = getOrdersByStatus("Out for Delivery")

    override fun getDeliveredOrders(): Flow<List<Order>> = getOrdersByStatus("Delivered")

    private fun getOrdersByStatus(status: String): Flow<List<Order>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = database.child("orders").orderByChild("deliveryPersonId").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull {
                    it.getValue(Order::class.java)
                }.filter {
                    it.status == status
                }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }
}
