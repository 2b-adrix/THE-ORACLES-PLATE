package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetBannersUseCase(private val repository: HomeRepository) {

    operator fun invoke(): Flow<List<Int>> {
        return repository.getBanners()
    }
}
