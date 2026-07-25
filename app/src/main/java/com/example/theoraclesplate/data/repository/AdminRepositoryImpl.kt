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
import kotlinx.coroutines.launch
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
        val analyticsData = mutableMapOf<String, Any>()

        val usersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                analyticsData["totalUsers"] = users.size
                analyticsData["totalSellers"] = users.count { it.role == "seller" }
                analyticsData["pendingSellers"] = users.count { it.role == "seller" && it.status == "pending" }
                if (analyticsData.containsKey("totalOrders")) {
                    trySend(Result.success(analyticsData))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
                close(error.toException())
            }
        }

        val ordersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                analyticsData["totalOrders"] = orders.size
                analyticsData["totalRevenue"] = orders.sumOf { it.totalAmount }
                if (analyticsData.containsKey("totalUsers")) {
                    trySend(Result.success(analyticsData))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
                close(error.toException())
            }
        }

        usersRef.addValueEventListener(usersListener)
        ordersRef.addValueEventListener(ordersListener)

        awaitClose {
            usersRef.removeEventListener(usersListener)
            ordersRef.removeEventListener(ordersListener)
        }
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

    override fun getAllMenuItems(): Flow<Result<List<Pair<String, FoodItem>>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.flatMap { sellerSnapshot ->
                    val sellerId = sellerSnapshot.key ?: ""
                    sellerSnapshot.children.mapNotNull { 
                        val menuItem = it.getValue(FoodItem::class.java)
                        menuItem?.id = it.key ?: ""
                        menuItem?.let { item -> sellerId to item }
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

    override suspend fun updateUserRole(userId: String, role: String) {
        withContext(Dispatchers.IO) {
            usersRef.child(userId).child("role").setValue(role).await()
        }
    }
}