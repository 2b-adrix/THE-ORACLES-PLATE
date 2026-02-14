package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    fun getMenuItems(sellerId: String): Flow<Result<List<Pair<String, FoodItem>>>>

    fun getAllMenuItems(): Flow<Result<List<FoodItem>>>

    suspend fun addMenuItem(sellerId: String, menuItem: FoodItem)

    suspend fun deleteMenuItem(sellerId: String, menuItemId: String)

    suspend fun updateMenuItem(sellerId: String, menuItemId: String, foodItem: FoodItem)
}
