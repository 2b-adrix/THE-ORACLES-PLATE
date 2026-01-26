package com.example.theoraclesplate.di

import android.content.Context
import com.example.theoraclesplate.data.repository.AdminRepositoryImpl
import com.example.theoraclesplate.data.repository.AuthRepositoryImpl
import com.example.theoraclesplate.data.repository.CartRepositoryImpl
import com.example.theoraclesplate.data.repository.CheckoutRepositoryImpl
import com.example.theoraclesplate.data.repository.DeliveryRepositoryImpl
import com.example.theoraclesplate.data.repository.DetailsRepositoryImpl
import com.example.theoraclesplate.data.repository.HistoryRepositoryImpl
import com.example.theoraclesplate.data.repository.HomeRepositoryImpl
import com.example.theoraclesplate.data.repository.MenuRepositoryImpl
import com.example.theoraclesplate.data.repository.OrderRepositoryImpl
import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.domain.repository.CartRepository
import com.example.theoraclesplate.domain.repository.CheckoutRepository
import com.example.theoraclesplate.domain.repository.DeliveryRepository
import com.example.theoraclesplate.domain.repository.DetailsRepository
import com.example.theoraclesplate.domain.repository.HistoryRepository
import com.example.theoraclesplate.domain.repository.HomeRepository
import com.example.theoraclesplate.domain.repository.MenuRepository
import com.example.theoraclesplate.domain.repository.OrderRepository
import com.example.theoraclesplate.domain.use_case.*
import com.example.theoraclesplate.service.CloudinaryImageUploader
import com.example.theoraclesplate.service.ImageUploader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageUploader(@ApplicationContext context: Context): ImageUploader {
        return CloudinaryImageUploader(context)
    }

    @Provides
    @Singleton
    fun provideMenuRepository(): MenuRepository {
        return MenuRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideMenuUseCases(repository: MenuRepository): MenuUseCases {
        return MenuUseCases(
            getMenuItems = GetMenuItemsUseCase(repository),
            addMenuItem = AddMenuItemUseCase(repository),
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
            getDeliveryUsers = GetDeliveryUsersUseCase(repository),
            getAnalyticsData = GetAnalyticsDataUseCase(repository),
            getAllMenuItems = GetAllMenuItemsUseCase(repository)
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
            getCurrentUser = GetCurrentUserUseCase(repository),
            loginWithGoogle = LoginWithGoogleUseCase(repository),
            createUser = CreateUserUseCase(repository),
            getUserRole = GetUserRoleUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(): DeliveryRepository {
        return DeliveryRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideDeliveryUseCases(repository: DeliveryRepository): DeliveryUseCases {
        return DeliveryUseCases(
            getReadyForPickupOrders = GetReadyForPickupOrdersUseCase(repository),
            getOutForDeliveryOrders = GetOutForDeliveryOrdersUseCase(repository),
            getDeliveredOrders = GetDeliveredOrdersUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository {
        return HomeRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(repository: HomeRepository): HomeUseCases {
        return HomeUseCases(
            getBanners = GetBannersUseCase(repository),
            getPopularFood = GetPopularFoodUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository {
        return CartRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideCartUseCases(repository: CartRepository): CartUseCases {
        return CartUseCases(
            getCartItems = GetCartItemsUseCase(repository),
            addToCart = AddToCartUseCase(repository),
            removeFromCart = RemoveFromCartUseCase(repository),
            updateQuantity = UpdateQuantityUseCase(repository),
            getCartItem = GetCartItemUseCase(repository),
            clearCart = ClearCartUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDetailsRepository(): DetailsRepository {
        return DetailsRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideDetailsUseCases(repository: DetailsRepository): DetailsUseCases {
        return DetailsUseCases(
            getFoodItemDetails = GetFoodItemDetailsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCheckoutRepository(): CheckoutRepository {
        return CheckoutRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideCheckoutUseCases(repository: CheckoutRepository): CheckoutUseCases {
        return CheckoutUseCases(
            createOrder = CreateOrderUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideHistoryUseCases(repository: HistoryRepository): HistoryUseCases {
        return HistoryUseCases(
            getOrderHistory = GetOrderHistoryUseCase(repository)
        )
    }
}
