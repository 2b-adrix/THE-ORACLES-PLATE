package com.example.theoraclesplate.data.repository

import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.model.CartItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : CartRepository {

    private fun getCartRef(userId: String) = database.reference.child("carts").child(userId)

    override fun getCartItems(userId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                trySend(cartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        getCartRef(userId).addValueEventListener(listener)
        awaitClose { getCartRef(userId).removeEventListener(listener) }
    }

    override suspend fun addToCart(userId: String, cartItem: CartItem) = withContext(Dispatchers.IO) {
        getCartRef(userId).child(cartItem.id).setValue(cartItem).await()
    }

    override suspend fun removeFromCart(userId: String, cartItemId: String) = withContext(Dispatchers.IO) {
        getCartRef(userId).child(cartItemId).removeValue().await()
    }

    override suspend fun updateQuantity(userId: String, cartItemId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        getCartRef(userId).child(cartItemId).child("quantity").setValue(newQuantity).await()
    }

    override suspend fun getCartItem(userId: String, foodItemId: String): CartItem? = withContext(Dispatchers.IO) {
        val snapshot = getCartRef(userId).child(foodItemId).get().await()
        snapshot.getValue(CartItem::class.java)
    }

    override suspend fun clearCart(userId: String) = withContext(Dispatchers.IO) {
        getCartRef(userId).removeValue().await()
    }
}
