package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.domain.repository.GeocodingRepository
import com.example.theoraclesplate.domain.repository.OrderRepository

data class DeliveryUseCases(
    val getReadyForPickupOrders: GetReadyForPickupOrdersUseCase,
    val getOutForDeliveryOrders: GetOutForDeliveryOrdersUseCase,
    val getDeliveredOrders: GetDeliveredOrdersUseCase,
    val updateOrderStatus: UpdateOrderStatusUseCase,
    val getCoordinatesFromAddress: GetCoordinatesFromAddressUseCase,
    val acceptOrder: AcceptOrderUseCase
)
