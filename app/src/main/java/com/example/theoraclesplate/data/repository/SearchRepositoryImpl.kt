package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.SearchRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SearchRepositoryImpl : SearchRepository {

    private val database = Firebase.database.reference

    override fun searchMenuItems(query: String): Flow<Result<List<FoodItem>>> = flow {
        try {
            val snapshot = database.child("menu_items").get().await()
            val menuItems = mutableListOf<FoodItem>()
            for (sellerSnapshot in snapshot.children) {
                for (menuItemSnapshot in sellerSnapshot.children) {
                    val menuItem = menuItemSnapshot.getValue(FoodItem::class.java)
                    if (menuItem != null && menuItem.name.contains(query, ignoreCase = true)) {
                        menuItems.add(menuItem)
                    }
                }
            }
            emit(Result.success(menuItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
