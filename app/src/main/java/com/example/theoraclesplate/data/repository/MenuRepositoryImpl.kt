package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.MenuRepository
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : MenuRepository {

    private val dbRef = database.reference.child("menu_items")

    override fun getMenuItems(sellerId: String): Flow<Result<List<FoodItem>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.mapNotNull {
                    val menuItem = it.getValue(FoodItem::class.java)
                    menuItem?.id = it.key ?: ""
                    menuItem
                }
                trySend(Result.success(menuItems))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        dbRef.child(sellerId).addValueEventListener(listener)

        awaitClose { dbRef.child(sellerId).removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getAllMenuItems(): Flow<Result<List<FoodItem>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = snapshot.children.flatMap { sellerSnapshot ->
                    sellerSnapshot.children.mapNotNull {
                        val menuItem = it.getValue(FoodItem::class.java)
                        menuItem?.id = it.key ?: ""
                        menuItem
                    }
                }
                trySend(Result.success(menuItems))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }

        dbRef.addValueEventListener(listener)
        awaitClose { dbRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getMenuItem(foodItemId: String, sellerId: String): Flow<Result<FoodItem?>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItem = snapshot.getValue(FoodItem::class.java)
                menuItem?.id = snapshot.key ?: ""
                trySend(Result.success(menuItem))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        dbRef.child(sellerId).child(foodItemId).addValueEventListener(listener)
        awaitClose { dbRef.child(sellerId).child(foodItemId).removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override suspend fun addMenuItem(sellerId: String, menuItem: FoodItem) = withContext(Dispatchers.IO) {
        val key = dbRef.child(sellerId).push().key
        menuItem.id = key ?: ""
        dbRef.child(sellerId).child(menuItem.id).setValue(menuItem).await()
    }

    override suspend fun deleteMenuItem(sellerId: String, menuItemId: String) = withContext(Dispatchers.IO) {
        dbRef.child(sellerId).child(menuItemId).removeValue().await()
    }

    override suspend fun updateMenuItem(sellerId: String, menuItemId: String, foodItem: FoodItem) = withContext(Dispatchers.IO) {
        dbRef.child(sellerId).child(menuItemId).setValue(foodItem).await()
    }
}
