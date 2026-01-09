package com.example.theoraclesplate.ui.seller.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.OrderUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerOrdersViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SellerOrdersState())
    val state: State<SellerOrdersState> = _state

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
        authUseCases.getCurrentUser()?.uid?.let { sellerId ->
            orderUseCases.getOrdersForSeller(sellerId).onEach { orders ->
                _state.value = state.value.copy(orders = orders)
            }.launchIn(viewModelScope)
        }
    }
}

data class SellerOrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false
)

sealed class SellerOrdersEvent {
    data class UpdateOrderStatus(val orderId: String, val newStatus: String) : SellerOrdersEvent()
}
