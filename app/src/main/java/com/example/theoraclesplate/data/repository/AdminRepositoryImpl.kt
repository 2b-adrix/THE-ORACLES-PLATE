package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val firestore: FirebaseFirestore
) : AdminRepository {

    private val usersRef = database.reference.child("users")
    private val menuItemsRef = database.reference.child("menu_items")
    private val ordersRef = database.reference.child("orders")

    override fun getPendingSellers(): Flow<Result<List<User>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    user?.uid = it.key ?: ""
                    user
                }
                    .filter { it.role == "seller" && it.status == "pending" }
                trySend(Result.success(users))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override suspend fun approveSeller(userId: String) {
        withContext(Dispatchers.IO) {
            usersRef.child(userId).child("status").setValue("approved").await()
        }
    }

    override suspend fun declineSeller(userId: String) {
        withContext(Dispatchers.IO) {
            usersRef.child(userId).removeValue().await()
        }
    }

    override fun getAllUsers(): Flow<Result<List<User>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    user?.uid = it.key ?: ""
                    user
                }
                trySend(Result.success(users))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override fun getAllOrders(): Flow<Result<List<Order>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                trySend(Result.success(orders))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }

    override fun getAnalyticsData(): Flow<Result<Map<String, Any>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.child("users").children.mapNotNull { it.getValue(User::class.java) }
                val orders = snapshot.child("orders").children.mapNotNull { it.getValue(Order::class.java) }

                val totalUsers = users.size
                val totalSellers = users.count { it.role == "seller" }
                val totalOrders = orders.size
                val totalRevenue = orders.sumOf { it.totalAmount }
                val pendingSellers = users.count { it.role == "seller" && it.status == "pending" }

                val analyticsData = mapOf(
                    "totalUsers" to totalUsers,
                    "totalSellers" to totalSellers,
                    "totalOrders" to totalOrders,
                    "totalRevenue" to totalRevenue,
                    "pendingSellers" to pendingSellers
                )
                trySend(Result.success(analyticsData))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        database.reference.addValueEventListener(listener)
        awaitClose { database.reference.removeEventListener(listener) }
    }

    override fun getDeliveryUsers(): Flow<Result<List<User>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    val user = it.getValue(User::class.java)
                    user?.uid = it.key ?: ""
                    user
                }
                    .filter { it.role == "delivery" }
                trySend(Result.success(users))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override fun getAllMenuItems(): Flow<Result<List<FoodItem>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.flatMap { sellerSnapshot ->
                    sellerSnapshot.children.mapNotNull { 
                        val menuItem = it.getValue(FoodItem::class.java)
                        menuItem?.id = it.key ?: ""
                        menuItem
                    }
                }
                trySend(Result.success(menuItems))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        menuItemsRef.addValueEventListener(listener)
        awaitClose { menuItemsRef.removeEventListener(listener) }
    }

    override suspend fun deleteOrder(orderId: String) {
        withContext(Dispatchers.IO) {
            ordersRef.child(orderId).removeValue().await()
        }
    }

    override suspend fun deleteUser(userId: String) {
        withContext(Dispatchers.IO) {
            usersRef.child(userId).removeValue().await()
        }
    }

    override suspend fun deleteMenuItem(sellerId: String, menuItemId: String) {
        withContext(Dispatchers.IO) {
            menuItemsRef.child(sellerId).child(menuItemId).removeValue().await()
        }
    }
}