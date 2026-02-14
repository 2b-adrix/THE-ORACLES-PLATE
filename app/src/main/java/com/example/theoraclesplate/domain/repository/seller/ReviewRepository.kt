package com.example.theoraclesplate.domain.repository.seller

import com.example.theoraclesplate.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviews(): Flow<List<Review>>
}
