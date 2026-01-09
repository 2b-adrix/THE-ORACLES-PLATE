package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetPopularFoodUseCase(private val repository: HomeRepository) {

    operator fun invoke(): Flow<List<FoodItem>> {
        return repository.getPopularFood()
    }
}
