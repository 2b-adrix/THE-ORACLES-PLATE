package com.example.theoraclesplate.ui.history.viewmodel

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
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
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        val userId = authUseCases.getCurrentUser()?.uid
                        if (userId == null) {
                            _eventFlow.emit(UiEvent.ShowSnackbar("You need to be logged in to reorder."))
                            _state.value = state.value.copy(isLoading = false)
                            return@launch
                        }
                        event.order.items.forEach { 
                            val cartItem = CartItem(
                                id = it.id,
                                name = it.name,
                                price = it.price,
                                imageUrl = it.imageUrl,
                                quantity = it.quantity,
                                sellerId = it.sellerId
                            )
                            cartRepository.addToCart(userId, cartItem)
                        }
                        _eventFlow.emit(UiEvent.NavigateToCart)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "An unexpected error occurred."))
                    } finally {
                        _state.value = state.value.copy(isLoading = false)
                    }
                }
            }
            is HistoryEvent.CancelOrder -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        historyUseCases.cancelOrder(event.orderId)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Order cancelled successfully."))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Failed to cancel order."))
                    } finally {
                        _state.value = state.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun getOrderHistory() {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid
            if (userId == null) {
                _state.value = _state.value.copy(error = "User not found.", isLoading = false)
                return@launch
            }

            historyUseCases.getOrderHistory(userId)
                .onStart {
                    _state.value = _state.value.copy(isLoading = true)
                }
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load order history."
                    )
                }
                .collectLatest { orders ->
                    _state.value = _state.value.copy(
                        orders = orders,
                        isLoading = false
                    )
                }
        }
    }

    sealed class UiEvent {
        object NavigateToCart : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

data class HistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HistoryEvent {
    data class Reorder(val order: Order) : HistoryEvent()
    data class CancelOrder(val orderId: String) : HistoryEvent()
}
