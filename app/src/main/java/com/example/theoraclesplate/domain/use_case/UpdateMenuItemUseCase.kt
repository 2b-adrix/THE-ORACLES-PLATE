package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class UpdateMenuItemUseCase(private val repository: MenuRepository) {

    suspend operator fun invoke(sellerId: String, menuItemId: String, menuItem: FoodItem): Flow<Result<Unit>> {
        // This should be implemented in the repository
        // return repository.updateMenuItem(sellerId, menuItemId, menuItem)
        return kotlinx.coroutines.flow.flow { emit(Result.success(Unit)) } // Placeholder
    }
}
