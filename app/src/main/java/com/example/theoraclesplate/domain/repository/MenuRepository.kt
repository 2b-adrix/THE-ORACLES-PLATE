package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    fun getMyMenuItems(sellerId: String): Flow<List<Pair<String, FoodItem>>>

    suspend fun addMenuItem(item: FoodItem)

    suspend fun updateMenuItem(key: String, item: FoodItem)

    suspend fun deleteMenuItem(key: String)
}
