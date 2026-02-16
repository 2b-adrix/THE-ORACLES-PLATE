package com.example.theoraclesplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.HistoryUseCases
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCases: HistoryUseCases,
    private val authUseCases: AuthUseCases,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getOrderHistory()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.Reorder -> {
                viewModelScope.launch {
                    val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
                    event.order.items.forEach {
                        val cartItem = CartItem(
                            id = it.id,
                            name = it.name,
                            price = it.price,
                            imageUrl = it.image,
                            quantity = it.quantity,
                            sellerId = it.sellerId
                        )
                        cartRepository.addToCart(userId, cartItem)
                    }
                    _eventFlow.emit(UiEvent.NavigateToCart)
                }
            }
            is HistoryEvent.CancelOrder -> {
                viewModelScope.launch {
                    historyUseCases.cancelOrder(event.orderId)
                }
            }
        }
    }

    private fun getOrderHistory() {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
            historyUseCases.getOrderHistory(userId).collectLatest { orders ->
                _state.value = _state.value.copy(orders = orders)
            }
        }
    }

    sealed class UiEvent {
        object NavigateToCart : UiEvent()
    }
}

data class HistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
