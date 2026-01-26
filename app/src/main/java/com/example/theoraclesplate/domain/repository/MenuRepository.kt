package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    fun getMenuItems(sellerId: String): Flow<Result<List<Pair<String, FoodItem>>>>

    suspend fun addMenuItem(sellerId: String, menuItem: FoodItem): Flow<Result<Unit>>

    suspend fun deleteMenuItem(sellerId: String, menuItemId: String): Flow<Result<Unit>>

    suspend fun updateMenuItem(sellerId: String, menuItemId: String, foodItem: FoodItem): Flow<Result<Unit>>
}
