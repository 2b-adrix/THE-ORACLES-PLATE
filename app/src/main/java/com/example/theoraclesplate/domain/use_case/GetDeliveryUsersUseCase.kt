package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.User
import kotlinx.coroutines.flow.Flow

class GetDeliveryUsersUseCase(private val repository: AdminRepository) {

    operator fun invoke(): Flow<Result<List<Pair<String, User>>>> {
        return repository.getDeliveryUsers()
    }
}
