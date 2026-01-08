package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetMyMenuItemsUseCase(private val repository: MenuRepository) {

    operator fun invoke(sellerId: String): Flow<List<Pair<String, FoodItem>>> {
        return repository.getMyMenuItems(sellerId)
    }
}
