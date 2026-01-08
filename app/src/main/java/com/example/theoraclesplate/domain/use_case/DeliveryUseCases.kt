package com.example.theoraclesplate.domain.use_case

data class DeliveryUseCases(
    val getReadyForPickupOrders: GetReadyForPickupOrdersUseCase,
    val getOutForDeliveryOrders: GetOutForDeliveryOrdersUseCase,
    val getDeliveredOrders: GetDeliveredOrdersUseCase
)
