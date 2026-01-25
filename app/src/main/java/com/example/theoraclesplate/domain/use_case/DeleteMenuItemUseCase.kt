package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow

class DeleteMenuItemUseCase(private val repository: MenuRepository) {

    suspend operator fun invoke(sellerId: String, menuItemId: String): Flow<Result<Unit>> {
        return repository.deleteMenuItem(sellerId, menuItemId)
    }
}
