package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getBanners(): Flow<List<String>>

    fun getPopularFood(): Flow<List<FoodItem>>
}
