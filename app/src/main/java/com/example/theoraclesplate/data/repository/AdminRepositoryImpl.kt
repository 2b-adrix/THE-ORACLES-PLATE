package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AdminRepositoryImpl : AdminRepository {

    private val database = Firebase.database.reference

    override fun getPendingSellers(): Flow<Result<List<Pair<String, User>>>> = flow {
        try {
            val snapshot = database.child("users").orderByChild("role").equalTo("seller_pending").get().await()
            val users = snapshot.children.mapNotNull { 
                val user = it.getValue(User::class.java)
                val key = it.key
                if (user != null && key != null) {
                    key to user
                } else {
                    null
                }
            }
            emit(Result.success(users))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun approveSeller(userId: String) {
        database.child("users").child(userId).child("role").setValue("seller").await()
    }

    override suspend fun declineSeller(userId: String) {
        database.child("users").child(userId).removeValue().await()
    }

    override fun getAllUsers(): Flow<Result<List<Pair<String, User>>>> = flow {
        try {
            val snapshot = database.child("users").get().await()
            val users = snapshot.children.mapNotNull { 
                val user = it.getValue(User::class.java)
                val key = it.key
                if (user != null && key != null) {
                    key to user
                } else {
                    null
                }
            }
            emit(Result.success(users))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAllOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val snapshot = database.child("orders").get().await()
            val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
            emit(Result.success(orders))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAnalyticsData(): Flow<Result<Map<String, Any>>> = flow {
        try {
            val usersSnapshot = database.child("users").get().await()
            val ordersSnapshot = database.child("orders").get().await()
            val totalUsers = usersSnapshot.childrenCount
            val totalOrders = ordersSnapshot.childrenCount
            val totalRevenue = ordersSnapshot.children.sumOf { it.child("totalAmount").getValue(Double::class.java) ?: 0.0 }
            val data = mapOf(
                "totalUsers" to totalUsers,
                "totalOrders" to totalOrders,
                "totalRevenue" to totalRevenue
            )
            emit(Result.success(data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getDeliveryUsers(): Flow<Result<List<Pair<String, User>>>> = flow {
        try {
            val snapshot = database.child("users").orderByChild("role").equalTo("delivery").get().await()
            val users = snapshot.children.mapNotNull { 
                val user = it.getValue(User::class.java)
                val key = it.key
                if (user != null && key != null) {
                    key to user
                } else {
                    null
                }
            }
            emit(Result.success(users))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAllMenuItems(): Flow<Result<List<Pair<String, FoodItem>>>> = flow {
        try {
            val snapshot = database.child("menu_items").get().await()
            val menuItems = mutableListOf<Pair<String, FoodItem>>()
            for (sellerSnapshot in snapshot.children) {
                for (menuItemSnapshot in sellerSnapshot.children) {
                    val menuItem = menuItemSnapshot.getValue(FoodItem::class.java)
                    val key = menuItemSnapshot.key
                    if (menuItem != null && key != null) {
                        menuItems.add(key to menuItem)
                    }
                }
            }
            emit(Result.success(menuItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteOrder(orderId: String) {
        database.child("orders").child(orderId).removeValue().await()
    }

    override suspend fun deleteUser(userId: String) {
        database.child("users").child(userId).removeValue().await()
    }

    override suspend fun deleteMenuItem(sellerId: String, menuItemId: String) {
        database.child("menu_items").child(sellerId).child(menuItemId).removeValue().await()
    }
}
