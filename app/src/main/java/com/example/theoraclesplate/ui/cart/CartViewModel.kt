package com.example.theoraclesplate.ui.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.CartUseCases
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCases: CartUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(CartState())
    val state: State<CartState> = _state

    private var getCartItemsJob: Job? = null

    init {
        getCartItems()
    }

    fun onEvent(event: CartEvent) {
        val userId = authUseCases.getCurrentUser()?.uid
        if (userId == null) return

        when (event) {
            is CartEvent.AddToCart -> {
                viewModelScope.launch {
                    val existingCartItem = cartUseCases.getCartItem(userId, event.item.name)
                    if (existingCartItem != null) {
                        cartUseCases.updateQuantity(userId, existingCartItem.id, existingCartItem.quantity + 1)
                    } else {
                        val newCartItem = CartItem(
                            id = event.item.name, // Use food name as ID for simplicity
                            name = event.item.name,
                            price = event.item.price,
                            image = event.item.imageUrl,
                            quantity = 1,
                            sellerId = event.item.sellerId
                        )
                        cartUseCases.addToCart(userId, newCartItem)
                    }
                }
            }
            is CartEvent.RemoveFromCart -> {
                viewModelScope.launch {
                    cartUseCases.removeFromCart(userId, event.itemId)
                }
            }
            is CartEvent.UpdateQuantity -> {
                viewModelScope.launch {
                    cartUseCases.updateQuantity(userId, event.itemId, event.newQuantity)
                }
            }
        }
    }

    private fun getCartItems() {
        val userId = authUseCases.getCurrentUser()?.uid
        if (userId == null) return

        getCartItemsJob?.cancel()
        getCartItemsJob = cartUseCases.getCartItems(userId)
            .onEach { items ->
                val totalPrice = items.sumOf { it.price * it.quantity }
                _state.value = state.value.copy(
                    cartItems = items,
                    totalPrice = totalPrice,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }
}

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = true
)

sealed class CartEvent {
    data class AddToCart(val item: FoodItem) : CartEvent()
    data class RemoveFromCart(val itemId: String) : CartEvent()
    data class UpdateQuantity(val itemId: String, val newQuantity: Int) : CartEvent()
}
