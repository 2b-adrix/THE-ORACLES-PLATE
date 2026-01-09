package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.R
import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HomeRepositoryImpl : HomeRepository {

    private val database = Firebase.database.reference

    override fun getBanners(): Flow<List<Int>> = callbackFlow {
        val banners = listOf(R.drawable.banner1, R.drawable.banner2)
        trySend(banners)
        awaitClose { }
    }

    override fun getPopularFood(): Flow<List<FoodItem>> = callbackFlow {
        val menuRef = database.child("menu")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodItems = snapshot.children.mapNotNull { 
                    it.getValue(FoodItem::class.java)
                }
                trySend(foodItems)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        menuRef.addValueEventListener(listener)
        awaitClose { menuRef.removeEventListener(listener) }
    }
}
