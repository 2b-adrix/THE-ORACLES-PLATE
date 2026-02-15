package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.SearchRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : SearchRepository {

    private val menuItemsRef = database.reference.child("menu_items")

    override fun searchMenuItems(query: String): Flow<Result<List<FoodItem>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.flatMap { sellerSnapshot ->
                    sellerSnapshot.children.mapNotNull { 
                        val menuItem = it.getValue(FoodItem::class.java)
                        menuItem?.id = it.key ?: ""
                        menuItem
                    }
                }
                val filteredList = if (query.isBlank()) {
                    menuItems
                } else {
                    menuItems.filter { it.name.contains(query, ignoreCase = true) }
                }
                trySend(Result.success(filteredList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        menuItemsRef.addValueEventListener(listener)
        awaitClose { menuItemsRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)
}
