package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetAllMenuItemsUseCase(private val repository: MenuRepository) {

    operator fun invoke(): Flow<List<Pair<String, FoodItem>>> {
        return repository.getMenuItems()
    }
}
