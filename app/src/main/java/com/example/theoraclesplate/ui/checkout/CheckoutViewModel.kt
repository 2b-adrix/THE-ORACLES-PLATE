package com.example.theoraclesplate.ui.checkout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.CartUseCases
import com.example.theoraclesplate.domain.use_case.CheckoutUseCases
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val checkoutUseCases: CheckoutUseCases,
    private val cartUseCases: CartUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(CheckoutState())
    val state: State<CheckoutState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getCartItems()
    }

    fun onEvent(event: CheckoutEvent) {
        when(event) {
            is CheckoutEvent.AddressChanged -> {
                _state.value = state.value.copy(address = event.address)
            }
            is CheckoutEvent.PaymentMethodChanged -> {
                _state.value = state.value.copy(paymentMethod = event.method)
            }
            is CheckoutEvent.PlaceOrder -> {
                viewModelScope.launch {
                    try {
                        _state.value = state.value.copy(isLoading = true)
                        val user = authUseCases.getCurrentUser()
                        if (user != null) {
                            val orderItems = state.value.cartItems.map { cartItem ->
                                OrderItem(
                                    name = cartItem.name,
                                    price = cartItem.price,
                                    image = cartItem.image,
                                    quantity = cartItem.quantity,
                                    sellerId = cartItem.sellerId
                                )
                            }

                            if (orderItems.isEmpty()) {
                                _eventFlow.emit(UiEvent.ShowError("Cannot place an empty order."))
                                _state.value = state.value.copy(isLoading = false)
                                return@launch
                            }
                            
                            val order = Order(
                                orderId = UUID.randomUUID().toString(),
                                userId = user.uid,
                                userName = user.displayName ?: "",
                                items = orderItems,
                                totalAmount = state.value.totalAmount,
                                address = state.value.address,
                                paymentMethod = state.value.paymentMethod,
                                timestamp = System.currentTimeMillis()
                            )
                            checkoutUseCases.createOrder(order)
                            // Assuming you have a `clearCart` use case that takes a userId.
                            // You may need to create this if it doesn't exist.
                            cartUseCases.clearCart(user.uid)
                            _eventFlow.emit(UiEvent.OrderPlaced)
                            // isLoading is set back to false in getCartItems as the list is updated
                        } else {
                            _eventFlow.emit(UiEvent.ShowError("User not logged in."))
                            _state.value = state.value.copy(isLoading = false)
                        }
                    } catch (e: Exception) {
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowError(e.localizedMessage ?: "An unexpected error occurred."))
                    }
                }
            }
        }
    }

    private fun getCartItems() {
        authUseCases.getCurrentUser()?.uid?.let { userId ->
            cartUseCases.getCartItems(userId).onEach { items ->
                val total = items.sumOf { (it.price.replace("$", "").toDoubleOrNull() ?: 0.0) * it.quantity }
                _state.value = state.value.copy(
                    cartItems = items,
                    totalAmount = "$${String.format("%.2f", total)}",
                    isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    sealed class UiEvent {
        object OrderPlaced : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
}

data class CheckoutState(
    val cartItems: List<com.example.theoraclesplate.model.CartItem> = emptyList(),
    val totalAmount: String = "$0.00",
    val address: String = "",
    val paymentMethod: String = "COD",
    val isLoading: Boolean = true
)

sealed class CheckoutEvent {
    data class AddressChanged(val address: String) : CheckoutEvent()
    data class PaymentMethodChanged(val method: String) : CheckoutEvent()
    object PlaceOrder : CheckoutEvent()
}
