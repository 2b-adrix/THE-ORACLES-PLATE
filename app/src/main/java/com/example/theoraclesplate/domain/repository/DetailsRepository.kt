package com.example.theoraclesplate.domain.repository

import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {

    fun getFoodItemDetails(foodItemName: String): Flow<FoodItem?>
}
