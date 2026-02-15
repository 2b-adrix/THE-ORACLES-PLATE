package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getBanners(): Flow<Result<List<String>>>

    fun getPopularFood(): Flow<Result<List<FoodItem>>>
}
