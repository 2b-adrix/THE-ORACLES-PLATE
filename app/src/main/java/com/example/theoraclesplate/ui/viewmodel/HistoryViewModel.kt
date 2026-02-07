package com.example.theoraclesplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.HistoryUseCases
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCases: HistoryUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val historyState: StateFlow<HistoryState> = _historyState

    fun fetchOrderHistory() {
        val userId = authUseCases.getCurrentUser()?.uid ?: return
        historyUseCases.getOrderHistory(userId).onEach { result ->
            result.onSuccess {
                _historyState.value = HistoryState.Success(it.map { order -> order.toHistoryItem() })
            }.onFailure {
                _historyState.value = HistoryState.Error(it.message ?: "An unexpected error occurred")
            }
        }.launchIn(viewModelScope)
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                historyUseCases.cancelOrder(orderId)
                fetchOrderHistory()
            } catch (e: Exception) {
                _historyState.value = HistoryState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val items: List<HistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

data class HistoryItem(
    val orderId: String,
    val items: List<OrderItem>,
    val price: String,
    val date: String,
    val status: String
)

fun Order.toHistoryItem(): HistoryItem {
    return HistoryItem(
        orderId = orderId,
        items = items,
        price = "$${totalAmount}",
        date = java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(timestamp)),
        status = status
    )
}
