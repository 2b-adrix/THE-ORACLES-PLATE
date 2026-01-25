package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow

class GetAnalyticsDataUseCase(private val repository: AdminRepository) {

    operator fun invoke(): Flow<Result<Map<String, Any>>> {
        return repository.getAnalyticsData()
    }
}
