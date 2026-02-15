package com.example.theoraclesplate.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _cartState = MutableStateFlow(CartState())
    val cartState = _cartState.asStateFlow()

    init {
        getCartItems()
    }

    private fun getCartItems() {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
            cartRepository.getCartItems(userId).collectLatest { cartItems ->
                val totalPrice = cartItems.sumOf { it.price * it.quantity }
                _cartState.value = _cartState.value.copy(
                    cartItems = cartItems,
                    totalPrice = totalPrice
                )
            }
        }
    }

    fun onEvent(event: CartEvent) {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
            when (event) {
                is CartEvent.UpdateQuantity -> {
                    cartRepository.updateQuantity(userId, event.cartItemId, event.newQuantity)
                }
                is CartEvent.RemoveFromCart -> {
                    cartRepository.removeFromCart(userId, event.cartItemId)
                }
            }
        }
    }

    fun addToCart(foodItem: FoodItem) {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
            val existingCartItem = cartRepository.getCartItem(userId, foodItem.id)

            if (existingCartItem != null) {
                val newQuantity = existingCartItem.quantity + 1
                cartRepository.updateQuantity(userId, existingCartItem.id, newQuantity)
            } else {
                val cartItem = CartItem(
                    id = foodItem.id,
                    name = foodItem.name,
                    price = foodItem.price,
                    imageUrl = foodItem.imageUrl,
                    quantity = 1,
                    sellerId = foodItem.sellerId
                )
                cartRepository.addToCart(userId, cartItem)
            }
        }
    }

    fun getCartItemsCount(): Int {
        return _cartState.value.cartItems.sumOf { it.quantity }
    }
}

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalPrice: Double = 0.0
)
