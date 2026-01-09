package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.AnalyticsData
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class AdminRepositoryImpl : AdminRepository {

    private val database = Firebase.database.reference

    override fun getPendingSellers(): Flow<List<Pair<String, User>>> = callbackFlow {
        val usersRef = database.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    if (user != null && user.role == "seller" && user.status == "pending") {
                        Pair(it.key!!, user)
                    } else {
                        null
                    }
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override suspend fun approveSeller(sellerId: String) {
        database.child("users").child(sellerId).child("status").setValue("approved").await()
    }

    override fun getAllUsers(): Flow<List<Pair<String, User>>> = callbackFlow {
        val usersRef = database.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        Pair(it.key!!, user)
                    } else {
                        null
                    }
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override suspend fun deleteUser(userId: String) {
        database.child("users").child(userId).removeValue().await()
    }

    override fun getAllOrders(): Flow<List<Pair<String, Order>>> = callbackFlow {
        val ordersRef = database.child("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    val order = it.getValue(Order::class.java)
                    if (order != null) {
                        Pair(it.key!!, order)
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

    override suspend fun deleteOrder(orderId: String) {
        database.child("orders").child(orderId).removeValue().await()
    }

    override fun getDeliveryUsers(): Flow<List<Pair<String, User>>> = callbackFlow {
        val usersRef = database.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    if (user != null && user.role == "delivery") {
                        Pair(it.key!!, user)
                    } else {
                        null
                    }
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override fun getAnalyticsData(): Flow<AnalyticsData> {
        val ordersFlow = callbackFlow<List<Order>> { 
            val listener = database.child("orders").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                    trySend(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose { database.child("orders").removeEventListener(listener) }
        }

        val usersFlow = callbackFlow<Int> { 
            val listener = database.child("users").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose { database.child("users").removeEventListener(listener) }
        }

        return combine(ordersFlow, usersFlow) { orders, userCount ->
            val totalRevenue = orders.filter { it.status == "Completed" }.sumOf { it.totalAmount.replace("$", "").toDoubleOrNull() ?: 0.0 }
            val totalOrders = orders.size
            AnalyticsData(totalRevenue, totalOrders, userCount)
        }
    }
}
