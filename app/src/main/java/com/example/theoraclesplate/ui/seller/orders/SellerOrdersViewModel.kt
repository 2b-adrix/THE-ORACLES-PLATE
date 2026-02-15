package com.example.theoraclesplate.ui.seller.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.seller.SellerOrdersRepository
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerOrdersViewModel @Inject constructor(
    private val sellerOrdersRepository: SellerOrdersRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SellerOrdersState())
    val state = _state.asStateFlow()

    init {
        getOrders()
    }

    private fun getOrders() {
        viewModelScope.launch {
            sellerOrdersRepository.getOrders().collectLatest { orders ->
                _state.value = _state.value.copy(orders = orders)
            }
        }
    }

    fun onEvent(event: SellerOrdersEvent) {
        viewModelScope.launch {
            when (event) {
                is SellerOrdersEvent.UpdateOrderStatus -> {
                    sellerOrdersRepository.updateOrderStatus(event.orderId, event.newStatus)
                }
            }
        }
    }
}

data class SellerOrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SellerOrdersEvent {
    data class UpdateOrderStatus(val orderId: String, val newStatus: String) : SellerOrdersEvent()
}
