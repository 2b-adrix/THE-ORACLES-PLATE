package com.example.theoraclesplate.domain.use_case

data class AdminUseCases(
    val getPendingSellers: GetPendingSellersUseCase,
    val approveSeller: ApproveSellerUseCase,
    val getAllUsers: GetAllUsersUseCase,
    val deleteUser: DeleteUserUseCase,
    val getAllOrders: GetAllOrdersUseCase,
    val deleteOrder: DeleteOrderUseCase,
    val getDeliveryUsers: GetDeliveryUsersUseCase,
    val getAnalyticsData: GetAnalyticsDataUseCase,
    val getAllMenuItems: GetAllMenuItemsUseCase
)
