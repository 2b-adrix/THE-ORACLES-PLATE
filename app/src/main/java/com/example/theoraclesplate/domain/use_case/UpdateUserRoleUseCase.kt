package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.AdminRepository
import javax.inject.Inject

class UpdateUserRoleUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(userId: String, role: String) {
        repository.updateUserRole(userId, role)
    }
}
