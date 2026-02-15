package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(private val menuRepository: MenuRepository, private val database: FirebaseDatabase) : HomeRepository {

    override fun getBanners(): Flow<Result<List<String>>> = callbackFlow {
        val bannersRef = database.reference.child("banners")
        val listener = bannersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banners = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                trySend(Result.success(banners))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        })
        awaitClose { bannersRef.removeEventListener(listener) }
    }

    override fun getPopularFood(): Flow<Result<List<FoodItem>>> {
        return menuRepository.getAllMenuItems()
    }
}
