package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.use_case.admin.DeleteMenuItemUseCase

data class AdminUseCases(
    val getPendingSellers: GetPendingSellersUseCase,
    val approveSeller: ApproveSellerUseCase,
    val declineSeller: DeclineSellerUseCase,
    val getAllUsers: GetAllUsersUseCase,
    val deleteUser: DeleteUserUseCase,
    val getAllOrders: GetAllOrdersUseCase,
    val deleteOrder: DeleteOrderUseCase,
    val getDeliveryUsers: GetDeliveryUsersUseCase,
    val getAnalyticsData: GetAnalyticsDataUseCase,
    val getAllMenuItems: GetAllMenuItemsUseCase,
    val deleteMenuItem: DeleteMenuItemUseCase
)
