package com.example.theoraclesplate.domain.use_case

data class OrderUseCases(
    val getOrdersForSeller: GetOrdersForSellerUseCase,
    val updateOrderStatus: UpdateOrderStatusUseCase
)
