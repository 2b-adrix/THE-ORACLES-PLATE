package com.example.theoraclesplate.ui.cart

sealed class CartEvent {
    data class UpdateQuantity(val cartItemId: String, val newQuantity: Int) : CartEvent()
    data class RemoveFromCart(val cartItemId: String) : CartEvent()
}
