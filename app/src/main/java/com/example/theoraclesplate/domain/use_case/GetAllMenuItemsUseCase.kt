package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class GetAllMenuItemsUseCase(private val repository: AdminRepository) {

    operator fun invoke(): Flow<Result<List<Pair<String, FoodItem>>>> {
        return repository.getAllMenuItems()
    }
}
