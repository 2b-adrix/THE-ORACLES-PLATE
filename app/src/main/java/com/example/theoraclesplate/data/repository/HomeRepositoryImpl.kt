package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class HomeRepositoryImpl(private val menuRepository: MenuRepository) : HomeRepository {

    private val database = Firebase.database.reference

    override fun getBanners(): Flow<List<String>> = flow {
        val banners = listOf(
            "https://firebasestorage.googleapis.com/v0/b/the-oracles-plate.appspot.com/o/banners%2Fbanner1.jpg?alt=media&token=e0a1f1b0-1b1a-4b0a-8b0a-1a1b1a1b1a1b",
            "https://firebasestorage.googleapis.com/v0/b/the-oracles-plate.appspot.com/o/banners%2Fbanner2.jpg?alt=media&token=e0a1f1b0-1b1a-4b0a-8b0a-1a1b1a1b1a1b",
            "https://firebasestorage.googleapis.com/v0/b/the-oracles-plate.appspot.com/o/banners%2Fbanner3.jpg?alt=media&token=e0a1f1b0-1b1a-4b0a-8b0a-1a1b1a1b1a1b"
        )
        emit(banners)
    }

    override fun getPopularFood(): Flow<List<FoodItem>> = flow {
        menuRepository.getAllMenuItems().collect {
            emit(it.getOrDefault(emptyList()))
        }
    }
}
