package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem

class UpdateMenuItemUseCase(private val repository: MenuRepository) {

    suspend operator fun invoke(key: String, item: FoodItem) {
        repository.updateMenuItem(key, item)
    }
}
