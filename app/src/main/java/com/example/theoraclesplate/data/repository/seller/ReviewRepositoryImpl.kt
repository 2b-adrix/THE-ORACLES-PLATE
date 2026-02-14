package com.example.theoraclesplate.data.repository.seller

import com.example.theoraclesplate.domain.repository.seller.ReviewRepository
import com.example.theoraclesplate.model.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ReviewRepositoryImpl : ReviewRepository {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    override fun getReviews(): Flow<List<Review>> = callbackFlow {
        val sellerId = auth.currentUser?.uid ?: run {
            close(Exception("User not logged in"))
            return@callbackFlow
        }

        // This is a placeholder implementation. You will need to adjust your database structure
        // to support querying reviews by seller.
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                trySend(reviews)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child("reviews").child(sellerId).addValueEventListener(listener)

        awaitClose { database.child("reviews").child(sellerId).removeEventListener(listener) }
    }
}
