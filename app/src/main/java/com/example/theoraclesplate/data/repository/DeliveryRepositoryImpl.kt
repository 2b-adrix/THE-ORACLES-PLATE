package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DeliveryRepositoryImpl : DeliveryRepository {

    private val database = Firebase.database.reference

    override fun getReadyForPickupOrders(): Flow<List<Order>> = callbackFlow {
        val ordersRef = database.child("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    val order = it.getValue(Order::class.java)
                    if (order != null && order.status == "Ready") {
                        order
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

    override fun getOutForDeliveryOrders(): Flow<List<Order>> = callbackFlow {
        val ordersRef = database.child("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    val order = it.getValue(Order::class.java)
                    if (order != null && order.status == "Out for Delivery") {
                        order
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

    override fun getDeliveredOrders(): Flow<List<Order>> = callbackFlow {
        val ordersRef = database.child("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    val order = it.getValue(Order::class.java)
                    if (order != null && order.status == "Delivered") {
                        order
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
}
