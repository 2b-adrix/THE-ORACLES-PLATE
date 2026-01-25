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

    override suspend fun approveSeller(userId: String): Flow<Result<Unit>> = flow {
        try {
            database.child("users").child(userId).child("role").setValue("seller").await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun declineSeller(userId: String): Flow<Result<Unit>> = flow {
        try {
            database.child("users").child(userId).removeValue().await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAllUsers(): Flow<Result<List<Pair<String, User>>>> = flow {
        // TODO: Implement
        emit(Result.success(emptyList()))
    }

    override fun getAllOrders(): Flow<Result<List<Order>>> = flow {
        // TODO: Implement
        emit(Result.success(emptyList()))
    }

    override fun getAnalyticsData(): Flow<Result<Map<String, Any>>> = flow {
        // TODO: Implement
        emit(Result.success(emptyMap()))
    }

    override fun getDeliveryUsers(): Flow<Result<List<Pair<String, User>>>> = flow {
        // TODO: Implement
        emit(Result.success(emptyList()))
    }

    override fun getAllMenuItems(): Flow<Result<List<FoodItem>>> = flow {
        try {
            val snapshot = database.child("menu_items").get().await()
            val menuItems = mutableListOf<FoodItem>()
            for (sellerSnapshot in snapshot.children) {
                for (menuItemSnapshot in sellerSnapshot.children) {
                    val menuItem = menuItemSnapshot.getValue(FoodItem::class.java)
                    if (menuItem != null) {
                        menuItems.add(menuItem)
                    }
                }
            }
            emit(Result.success(menuItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteOrder(orderId: String): Flow<Result<Unit>> = flow {
        // TODO: Implement
        emit(Result.success(Unit))
    }

    override suspend fun deleteUser(userId: String): Flow<Result<Unit>> = flow {
        // TODO: Implement
        emit(Result.success(Unit))
    }
}
