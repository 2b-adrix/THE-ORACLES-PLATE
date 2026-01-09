package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.DetailsRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DetailsRepositoryImpl : DetailsRepository {

    private val database = Firebase.database.reference

    override fun getFoodItemDetails(foodItemName: String): Flow<FoodItem?> = callbackFlow {
        val menuRef = database.child("menu").orderByChild("name").equalTo(foodItemName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.children.firstOrNull()?.getValue(FoodItem::class.java)
                trySend(item)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        menuRef.addValueEventListener(listener)
        awaitClose { menuRef.removeEventListener(listener) }
    }
}
