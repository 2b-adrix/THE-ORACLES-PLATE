package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMenuItems(query: String): Flow<Result<List<FoodItem>>>
}
