package com.example.theoraclesplate.data.repository.seller

import com.example.theoraclesplate.domain.repository.seller.ReviewRepository
import com.example.theoraclesplate.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ReviewRepository {

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
        database.reference.child("reviews").child(sellerId).addValueEventListener(listener)

        awaitClose { database.reference.child("reviews").child(sellerId).removeEventListener(listener) }
    }
}
