package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem

class AddMenuItemUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(sellerId: String, item: FoodItem) = repository.addMenuItem(sellerId, item)
}
