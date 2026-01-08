package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.AnalyticsData
import kotlinx.coroutines.flow.Flow

class GetAnalyticsDataUseCase(private val repository: AdminRepository) {

    operator fun invoke(): Flow<AnalyticsData> {
        return repository.getAnalyticsData()
    }
}
