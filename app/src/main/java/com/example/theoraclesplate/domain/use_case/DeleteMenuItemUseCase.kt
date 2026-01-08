package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository

class DeleteMenuItemUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(menuItemId: String) {
        repository.deleteMenuItem(menuItemId)
    }
}
