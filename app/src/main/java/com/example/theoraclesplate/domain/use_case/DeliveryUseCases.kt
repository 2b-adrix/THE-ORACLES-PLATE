package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.GeocodingRepository

data class DeliveryUseCases(
    val getReadyForPickupOrders: GetReadyForPickupOrdersUseCase,
    val getOutForDeliveryOrders: GetOutForDeliveryOrdersUseCase,
    val getDeliveredOrders: GetDeliveredOrdersUseCase,
    val updateOrderStatus: UpdateOrderStatusUseCase,
    val getCoordinatesFromAddress: GetCoordinatesFromAddressUseCase
)
