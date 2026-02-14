package com.example.theoraclesplate.di

import android.content.Context
import com.example.theoraclesplate.data.repository.AuthRepositoryImpl
import com.example.theoraclesplate.data.repository.MenuRepositoryImpl
import com.example.theoraclesplate.data.repository.OrderRepositoryImpl
import com.example.theoraclesplate.data.repository.UserRepositoryImpl
import com.example.theoraclesplate.data.repository.seller.ReviewRepositoryImpl
import com.example.theoraclesplate.data.repository.seller.SellerOrdersRepositoryImpl
import com.example.theoraclesplate.domain.repository.*
import com.example.theoraclesplate.domain.repository.seller.ReviewRepository
import com.example.theoraclesplate.domain.repository.seller.SellerOrdersRepository
import com.example.theoraclesplate.domain.use_case.*
import com.example.theoraclesplate.service.CloudinaryImageUploader
import com.example.theoraclesplate.service.ImageUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideMenuRepository(firebaseFirestore: FirebaseFirestore): MenuRepository {
        return MenuRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(firebaseFirestore: FirebaseFirestore): OrderRepository {
        return OrderRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firebaseFirestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideSellerOrdersRepository(firebaseFirestore: FirebaseFirestore): SellerOrdersRepository {
        return SellerOrdersRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(): ReviewRepository {
        return ReviewRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideImageUploader(@ApplicationContext context: Context): ImageUploader {
        return CloudinaryImageUploader(context)
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(authRepository: AuthRepository): AuthUseCases {
        return AuthUseCases(
            loginUser = LoginUserUseCase(authRepository),
            signupUser = SignupUserUseCase(authRepository),
            logoutUser = LogoutUserUseCase(authRepository),
            getCurrentUser = GetCurrentUserUseCase(authRepository),
            loginWithGoogle = LoginWithGoogleUseCase(authRepository),
            createUser = CreateUserUseCase(authRepository),
            getUserRole = GetUserRoleUseCase(authRepository)
        )
    }

    @Provides
    @Singleton
    fun provideAdminUseCases(adminRepository: AdminRepository): AdminUseCases {
        return AdminUseCases(
            getPendingSellers = GetPendingSellersUseCase(adminRepository),
            approveSeller = ApproveSellerUseCase(adminRepository),
            declineSeller = DeclineSellerUseCase(adminRepository),
            getAllUsers = GetAllUsersUseCase(adminRepository),
            deleteUser = DeleteUserUseCase(adminRepository),
            getAllOrders = GetAllOrdersUseCase(adminRepository),
            deleteOrder = DeleteOrderUseCase(adminRepository),
            getDeliveryUsers = GetDeliveryUsersUseCase(adminRepository),
            getAnalyticsData = GetAnalyticsDataUseCase(adminRepository),
            getAllMenuItems = GetAllMenuItemsUseCase(adminRepository),
            deleteMenuItem = DeleteMenuItemUseCase(adminRepository)
        )
    }

    @Provides
    @Singleton
    fun provideCartUseCases(cartRepository: CartRepository): CartUseCases {
        return CartUseCases(
            getCartItems = GetCartItemsUseCase(cartRepository),
            addToCart = AddToCartUseCase(cartRepository),
            removeFromCart = RemoveFromCartUseCase(cartRepository),
            updateQuantity = UpdateQuantityUseCase(cartRepository),
            getCartItem = GetCartItemUseCase(cartRepository),
            clearCart = ClearCartUseCase(cartRepository)
        )
    }

    @Provides
    @Singleton
    fun provideCheckoutUseCases(checkoutRepository: CheckoutRepository): CheckoutUseCases {
        return CheckoutUseCases(
            createOrder = CreateOrderUseCase(checkoutRepository)
        )
    }

    @Provides
    @Singleton
    fun provideDeliveryUseCases(deliveryRepository: DeliveryRepository): DeliveryUseCases {
        return DeliveryUseCases(
            getReadyForPickupOrders = GetReadyForPickupOrdersUseCase(deliveryRepository),
            getOutForDeliveryOrders = GetOutForDeliveryOrdersUseCase(deliveryRepository),
            getDeliveredOrders = GetDeliveredOrdersUseCase(deliveryRepository),
            updateOrderStatus = UpdateOrderStatusUseCase(deliveryRepository),
            getCoordinatesFromAddress = GetCoordinatesFromAddressUseCase(deliveryRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHistoryUseCases(historyRepository: HistoryRepository): HistoryUseCases {
        return HistoryUseCases(
            getOrderHistory = GetOrderHistoryUseCase(historyRepository),
            cancelOrder = CancelOrderUseCase(historyRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(homeRepository: HomeRepository): HomeUseCases {
        return HomeUseCases(
            getBanners = GetBannersUseCase(homeRepository),
            getPopularFood = GetPopularFoodUseCase(homeRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMenuUseCases(menuRepository: MenuRepository): MenuUseCases {
        return MenuUseCases(
            getMenuItems = GetMenuItemsUseCase(menuRepository),
            addMenuItem = AddMenuItemUseCase(menuRepository),
            deleteMenuItem = com.example.theoraclesplate.domain.use_case.DeleteMenuItemUseCase(menuRepository),
            updateMenuItem = UpdateMenuItemUseCase(menuRepository)
        )
    }
}
