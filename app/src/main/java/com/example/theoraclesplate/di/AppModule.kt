package com.example.theoraclesplate.di

import com.example.theoraclesplate.data.repository.AdminRepositoryImpl
import com.example.theoraclesplate.data.repository.AuthRepositoryImpl
import com.example.theoraclesplate.data.repository.MenuRepositoryImpl
import com.example.theoraclesplate.data.repository.OrderRepositoryImpl
import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMenuRepository(): MenuRepository {
        return MenuRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideMenuUseCases(repository: MenuRepository): MenuUseCases {
        return MenuUseCases(
            getMyMenuItems = GetMyMenuItemsUseCase(repository),
            addMenuItem = AddMenuItemUseCase(repository),
            updateMenuItem = UpdateMenuItemUseCase(repository),
            deleteMenuItem = DeleteMenuItemUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideOrderRepository(): OrderRepository {
        return OrderRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideOrderUseCases(repository: OrderRepository): OrderUseCases {
        return OrderUseCases(
            getOrdersForSeller = GetOrdersForSellerUseCase(repository),
            updateOrderStatus = UpdateOrderStatusUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAdminRepository(): AdminRepository {
        return AdminRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAdminUseCases(repository: AdminRepository): AdminUseCases {
        return AdminUseCases(
            getPendingSellers = GetPendingSellersUseCase(repository),
            approveSeller = ApproveSellerUseCase(repository),
            getAllUsers = GetAllUsersUseCase(repository),
            deleteUser = DeleteUserUseCase(repository),
            getAllOrders = GetAllOrdersUseCase(repository),
            deleteOrder = DeleteOrderUseCase(repository),
            getAllMenuItems = GetAllMenuItemsUseCase(repository),
            deleteMenuItem = DeleteMenuItemUseCase(repository),
            getDeliveryUsers = GetDeliveryUsersUseCase(repository),
            getAnalyticsData = GetAnalyticsDataUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthRepository): AuthUseCases {
        return AuthUseCases(
            loginUser = LoginUserUseCase(repository),
            signupUser = SignupUserUseCase(repository),
            logoutUser = LogoutUserUseCase(repository),
            getCurrentUser = GetCurrentUserUseCase(repository)
        )
    }
}
