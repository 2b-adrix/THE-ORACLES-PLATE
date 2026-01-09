package com.example.theoraclesplate.domain.use_case

data class CartUseCases(
    val getCartItems: GetCartItemsUseCase,
    val addToCart: AddToCartUseCase,
    val removeFromCart: RemoveFromCartUseCase,
    val updateQuantity: UpdateQuantityUseCase,
    val getCartItem: GetCartItemUseCase,
    val clearCart: ClearCartUseCase
)
