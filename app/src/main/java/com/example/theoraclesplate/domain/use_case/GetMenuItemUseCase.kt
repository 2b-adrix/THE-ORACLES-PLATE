package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetMenuItemUseCase(private val repository: MenuRepository) {

    operator fun invoke(foodItemId: String, sellerId: String): Flow<Result<FoodItem?>> {
        return repository.getMenuItem(foodItemId, sellerId)
    }
}
