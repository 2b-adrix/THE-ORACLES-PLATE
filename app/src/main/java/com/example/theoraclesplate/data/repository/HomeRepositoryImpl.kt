package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class HomeRepositoryImpl : HomeRepository {

    private val database = Firebase.database.reference

    override fun getBanners(): Flow<List<String>> = flow {
        // TODO: Implement
        emit(emptyList())
    }

    override fun getPopularFood(): Flow<List<FoodItem>> = flow {
        try {
            val snapshot = database.child("menu_items").limitToFirst(10).get().await()
            val foodItems = mutableListOf<FoodItem>()
            for (sellerSnapshot in snapshot.children) {
                for (menuItemSnapshot in sellerSnapshot.children) {
                    val foodItem = menuItemSnapshot.getValue(FoodItem::class.java)
                    if (foodItem != null) {
                        foodItems.add(foodItem)
                    }
                }
            }
            emit(foodItems)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
