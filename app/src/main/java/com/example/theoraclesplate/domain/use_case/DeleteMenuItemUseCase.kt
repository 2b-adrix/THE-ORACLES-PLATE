package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository

class DeleteMenuItemUseCase(private val repository: MenuRepository) {

    suspend operator fun invoke(sellerId: String, menuItemId: String) {
        repository.deleteMenuItem(sellerId, menuItemId)
    }
}
