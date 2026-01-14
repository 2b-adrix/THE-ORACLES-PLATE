package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    fun getMenuItems(): Flow<List<Pair<String, FoodItem>>>

    fun getMyMenuItems(sellerId: String): Flow<List<Pair<String, FoodItem>>>

    suspend fun addMenuItem(sellerId: String, item: FoodItem)

    suspend fun updateMenuItem(sellerId: String, itemId: String, item: FoodItem)

    suspend fun deleteMenuItem(sellerId: String, itemId: String)
}
