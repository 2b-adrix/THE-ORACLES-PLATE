package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    fun getMenuItems(sellerId: String): Flow<Result<List<FoodItem>>>

    fun getAllMenuItems(): Flow<Result<List<FoodItem>>>

    fun getMenuItem(foodItemId: String, sellerId: String): Flow<Result<FoodItem?>>

    suspend fun addMenuItem(sellerId: String, menuItem: FoodItem): Void?

    suspend fun deleteMenuItem(sellerId: String, menuItemId: String): Void?

    suspend fun updateMenuItem(sellerId: String, menuItemId: String, foodItem: FoodItem): Void?
}
