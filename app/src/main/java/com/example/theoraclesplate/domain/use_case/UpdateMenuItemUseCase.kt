package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem

class UpdateMenuItemUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(sellerId: String, menuItemId: String, foodItem: FoodItem) = repository.updateMenuItem(sellerId, menuItemId, foodItem)
}
