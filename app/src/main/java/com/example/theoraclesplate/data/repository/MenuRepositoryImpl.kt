package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MenuRepositoryImpl : MenuRepository {

    private val database = Firebase.database.reference

    override fun getMyMenuItems(sellerId: String): Flow<List<Pair<String, FoodItem>>> = callbackFlow {
        val menuRef = database.child("menu")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.mapNotNull { 
                    val item = it.getValue(FoodItem::class.java)
                    if (item != null && item.sellerId == sellerId) {
                        Pair(it.key!!, item)
                    } else {
                        null
                    }
                }
                trySend(menuItems)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        menuRef.addValueEventListener(listener)
        awaitClose { menuRef.removeEventListener(listener) }
    }

    override suspend fun addMenuItem(item: FoodItem) {
        database.child("menu").push().setValue(item).await()
    }

    override suspend fun updateMenuItem(key: String, item: FoodItem) {
        database.child("menu").child(key).setValue(item).await()
    }

    override suspend fun deleteMenuItem(key: String) {
        database.child("menu").child(key).removeValue().await()
    }
}
