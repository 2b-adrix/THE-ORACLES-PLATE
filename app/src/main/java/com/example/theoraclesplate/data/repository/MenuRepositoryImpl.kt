package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MenuRepositoryImpl : MenuRepository {

    private val database = Firebase.database.reference.child("menu_items")

    override fun getMenuItems(sellerId: String): Flow<Result<List<Pair<String, FoodItem>>>> = flow {
        try {
            val snapshot = database.child(sellerId).get().await()
            val menuItems = snapshot.children.mapNotNull { 
                val menuItem = it.getValue(FoodItem::class.java)
                val key = it.key
                if (menuItem != null && key != null) {
                    key to menuItem
                } else {
                    null
                }
            }
            emit(Result.success(menuItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAllMenuItems(): Flow<Result<List<FoodItem>>> = flow {
        try {
            val snapshot = database.get().await()
            val menuItems = snapshot.children.flatMap { sellerSnapshot ->
                sellerSnapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
            }
            emit(Result.success(menuItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addMenuItem(sellerId: String, menuItem: FoodItem) {
        database.child(sellerId).push().setValue(menuItem).await()
    }

    override suspend fun deleteMenuItem(sellerId: String, menuItemId: String) {
        database.child(sellerId).child(menuItemId).removeValue().await()
    }

    override suspend fun updateMenuItem(sellerId: String, menuItemId: String, foodItem: FoodItem) {
        database.child(sellerId).child(menuItemId).setValue(foodItem).await()
    }
}
