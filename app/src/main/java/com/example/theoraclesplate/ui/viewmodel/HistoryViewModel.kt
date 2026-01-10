package com.example.theoraclesplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {

    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val historyState = _historyState.asStateFlow()

    fun fetchOrderHistory() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val historyRef = database.child("users").child(currentUser.uid).child("order_history")
                historyRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val historyItems = snapshot.children.mapNotNull {
                            val order = it.getValue(Order::class.java)
                            order?.let { o -> toHistoryItem(o, it.key) }
                        }.sortedByDescending { it.timestamp }
                        _historyState.value = HistoryState.Success(historyItems)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _historyState.value = HistoryState.Error(error.message)
                    }
                })
            } else {
                _historyState.value = HistoryState.Error("User not logged in")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            database.child("orders").child(orderId).child("status").setValue("Cancelled")
            database.child("users").child(currentUser.uid).child("order_history").child(orderId).child("status").setValue("Cancelled")
        }
    }

    private fun toHistoryItem(order: Order, orderId: String?): HistoryItem {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = dateFormat.format(Date(order.timestamp))
        val displayName = if (order.items.size > 1) {
            "${order.items.first().name} + ${order.items.size - 1} more"
        } else {
            order.items.firstOrNull()?.name ?: "Food Item"
        }
        val image = order.items.firstOrNull()?.image ?: ""

        return HistoryItem(
            orderId = orderId ?: "",
            name = displayName,
            price = "$${order.totalAmount}",
            date = date,
            image = image,
            status = order.status,
            timestamp = order.timestamp
        )
    }
}

data class HistoryItem(
    val orderId: String,
    val name: String,
    val price: String,
    val date: String,
    val image: String,
    val status: String,
    val timestamp: Long
)

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val items: List<HistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}
