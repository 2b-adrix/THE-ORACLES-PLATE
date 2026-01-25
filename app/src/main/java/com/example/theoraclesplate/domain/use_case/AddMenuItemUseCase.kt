package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class AddMenuItemUseCase(private val repository: MenuRepository) {

    suspend operator fun invoke(sellerId: String, menuItem: FoodItem): Flow<Result<Unit>> {
        return repository.addMenuItem(sellerId, menuItem)
    }
}
