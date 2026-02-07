package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.SearchRepository
import com.example.theoraclesplate.model.FoodItem
import kotlinx.coroutines.flow.Flow

class SearchMenuItemsUseCase(private val repository: SearchRepository) {

    operator fun invoke(query: String): Flow<Result<List<FoodItem>>> {
        return repository.searchMenuItems(query)
    }
}
