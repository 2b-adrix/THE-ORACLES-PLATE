package com.example.theoraclesplate.di

import android.content.Context
import com.example.theoraclesplate.data.repository.*
import com.example.theoraclesplate.data.repository.seller.ReviewRepositoryImpl
import com.example.theoraclesplate.data.repository.seller.SellerOrdersRepositoryImpl
import com.example.theoraclesplate.domain.repository.*
import com.example.theoraclesplate.domain.repository.seller.ReviewRepository
import com.example.theoraclesplate.domain.repository.seller.SellerOrdersRepository
import com.example.theoraclesplate.domain.use_case.*
import com.example.theoraclesplate.service.CloudinaryImageUploader
import com.example.theoraclesplate.service.ImageUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, database: FirebaseDatabase): AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideMenuRepository(database: FirebaseDatabase): MenuRepository {
        return MenuRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(database: FirebaseDatabase): OrderRepository {
        return OrderRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSellerOrdersRepository(database: FirebaseDatabase): SellerOrdersRepository {
        return SellerOrdersRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(database: FirebaseDatabase): ReviewRepository {
        return ReviewRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(database: FirebaseDatabase, firestore: FirebaseFirestore): AdminRepository {
        return AdminRepositoryImpl(database, firestore)
    }

    @Provides
    @Singleton
    fun provideCartRepository(database: FirebaseDatabase): CartRepository {
        return CartRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideCheckoutRepository(database: FirebaseDatabase): CheckoutRepository {
        return CheckoutRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(database: FirebaseDatabase): DeliveryRepository {
        return DeliveryRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(@ApplicationContext context: Context): GeocodingRepository {
        return GeocodingRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(database: FirebaseDatabase): HistoryRepository {
        return HistoryRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(menuRepository: MenuRepository, database: FirebaseDatabase): HomeRepository {
        return HomeRepositoryImpl(menuRepository, database)
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
            deleteMenuItem = com.example.theoraclesplate.domain.use_case.admin.DeleteMenuItemUseCase(adminRepository)
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
    fun provideCheckoutUseCases(orderRepository: OrderRepository): CheckoutUseCases {
        return CheckoutUseCases(
            placeOrder = PlaceOrderUseCase(orderRepository)
        )
    }

    @Provides
    @Singleton
    fun provideDeliveryUseCases(
        deliveryRepository: DeliveryRepository,
        orderRepository: OrderRepository,
        geocodingRepository: GeocodingRepository
    ): DeliveryUseCases {
        return DeliveryUseCases(
            getReadyForPickupOrders = GetReadyForPickupOrdersUseCase(deliveryRepository),
            getOutForDeliveryOrders = GetOutForDeliveryOrdersUseCase(deliveryRepository),
            getDeliveredOrders = GetDeliveredOrdersUseCase(deliveryRepository),
            updateOrderStatus = UpdateOrderStatusUseCase(orderRepository),
            getCoordinatesFromAddress = GetCoordinatesFromAddressUseCase(geocodingRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHistoryUseCases(
        historyRepository: HistoryRepository,
        orderRepository: OrderRepository
    ): HistoryUseCases {
        return HistoryUseCases(
            getOrderHistory = GetOrderHistoryUseCase(historyRepository),
            cancelOrder = CancelOrderUseCase(orderRepository)
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
            deleteMenuItem = DeleteMenuItemUseCase(menuRepository),
            updateMenuItem = UpdateMenuItemUseCase(menuRepository),
            getMenuItem = GetMenuItemUseCase(menuRepository)
        )
    }
}