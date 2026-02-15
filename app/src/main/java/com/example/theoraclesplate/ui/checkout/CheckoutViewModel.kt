package com.example.theoraclesplate.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.CheckoutUseCases
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val checkoutUseCases: CheckoutUseCases,
    private val cartRepository: CartRepository,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.PlaceOrder -> {
                viewModelScope.launch {
                    val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
                    val cartItems = cartRepository.getCartItems(userId).first()
                    val orderItems = cartItems.map {
                        OrderItem(
                            name = it.name,
                            price = it.price,
                            image = it.imageUrl,
                            quantity = it.quantity,
                            sellerId = it.sellerId
                        )
                    }
                    val totalAmount = cartItems.sumOf { it.price * it.quantity }

                    val order = Order(
                        orderId = UUID.randomUUID().toString(),
                        userId = userId,
                        userName = authUseCases.getCurrentUser()?.displayName ?: "",
                        items = orderItems,
                        totalAmount = totalAmount,
                        address = event.address,
                        timestamp = System.currentTimeMillis()
                    )
                    _state.value = _state.value.copy(isLoading = true)
                    try {
                        checkoutUseCases.placeOrder(order)
                        cartRepository.clearCart(userId)
                        _state.value = _state.value.copy(isLoading = false, orderPlaced = true)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Order placed successfully!"))
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Error placing order"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

data class CheckoutState(
    val isLoading: Boolean = false,
    val orderPlaced: Boolean = false,
    val error: String? = null
)

sealed class CheckoutEvent {
    data class PlaceOrder(val address: String) : CheckoutEvent()
}
