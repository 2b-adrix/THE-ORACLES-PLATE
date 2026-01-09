package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.model.CartItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl : CartRepository {

    private val database = Firebase.database.reference

    override fun getCartItems(userId: String): Flow<List<CartItem>> = callbackFlow {
        val cartRef = database.child("users").child(userId).child("cart")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        cartRef.addValueEventListener(listener)
        awaitClose { cartRef.removeEventListener(listener) }
    }

    override suspend fun addToCart(userId: String, cartItem: CartItem) {
        database.child("users").child(userId).child("cart").child(cartItem.id).setValue(cartItem).await()
    }

    override suspend fun removeFromCart(userId: String, cartItemId: String) {
        database.child("users").child(userId).child("cart").child(cartItemId).removeValue().await()
    }

    override suspend fun updateQuantity(userId: String, cartItemId: String, newQuantity: Int) {
        database.child("users").child(userId).child("cart").child(cartItemId).child("quantity").setValue(newQuantity).await()
    }

    override suspend fun getCartItem(userId: String, foodItemId: String): CartItem? {
        return database.child("users").child(userId).child("cart").child(foodItemId).get().await().getValue(CartItem::class.java)
    }

    override suspend fun clearCart(userId: String) {
        database.child("users").child(userId).child("cart").removeValue().await()
    }
}
