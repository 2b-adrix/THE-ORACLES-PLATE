package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.DetailsRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DetailsRepositoryImpl : DetailsRepository {

    private val database = Firebase.database.reference

    override fun getFoodItemDetails(foodItemName: String): Flow<FoodItem?> = flow {
        try {
            val snapshot = database.child("menu_items").get().await()
            var foodItem: FoodItem? = null
            for (sellerSnapshot in snapshot.children) {
                for (menuItemSnapshot in sellerSnapshot.children) {
                    val item = menuItemSnapshot.getValue(FoodItem::class.java)
                    if (item?.name == foodItemName) {
                        foodItem = item
                        break
                    }
                }
                if (foodItem != null) {
                    break
                }
            }
            emit(foodItem)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
