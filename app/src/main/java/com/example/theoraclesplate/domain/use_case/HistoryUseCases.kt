package com.example.theoraclesplate.domain.use_case

data class HistoryUseCases(
    val getOrderHistory: GetOrderHistoryUseCase,
    val cancelOrder: CancelOrderUseCase
)
