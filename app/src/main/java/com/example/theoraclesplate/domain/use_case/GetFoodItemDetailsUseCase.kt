package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.DetailsRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetFoodItemDetailsUseCase(private val repository: DetailsRepository) {

    operator fun invoke(foodItemName: String): Flow<FoodItem?> {
        return repository.getFoodItemDetails(foodItemName)
    }
}
