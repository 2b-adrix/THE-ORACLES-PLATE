package com.example.theoraclesplate.ui.seller.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.OrderUseCases
import com.example.theoraclesplate.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerOrdersViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SellerOrdersState())
    val state: State<SellerOrdersState> = _state

    private var getOrdersJob: Job? = null

    private val currentUserId: String? = Firebase.auth.currentUser?.uid

    init {
        getOrders()
    }

    fun onEvent(event: SellerOrdersEvent) {
        when (event) {
            is SellerOrdersEvent.UpdateOrderStatus -> {
                viewModelScope.launch {
                    orderUseCases.updateOrderStatus(event.orderId, event.newStatus)
                }
            }
        }
    }

    private fun getOrders() {
        getOrdersJob?.cancel()
        currentUserId?.let {
            getOrdersJob = orderUseCases.getOrdersForSeller(it)
                .onEach { orders ->
                    _state.value = state.value.copy(orders = orders, isLoading = false)
                }
                .launchIn(viewModelScope)
        }
    }
}

data class SellerOrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

sealed class SellerOrdersEvent {
    data class UpdateOrderStatus(val orderId: String, val newStatus: String) : SellerOrdersEvent()
}
