package com.example.theoraclesplate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.theoraclesplate.ui.admin.presentation.AdminDashboardScreen
import com.example.theoraclesplate.ui.admin.presentation.AdminPanelScreen
import com.example.theoraclesplate.ui.admin.allmenuitems.presentation.AllMenuItemsScreen
import com.example.theoraclesplate.ui.admin.allorders.presentation.AllOrdersScreen
import com.example.theoraclesplate.ui.admin.allusers.presentation.AllUsersScreen
import com.example.theoraclesplate.ui.admin.analytics.presentation.AnalyticsScreen
import com.example.theoraclesplate.ui.admin.deliverymanagement.presentation.DeliveryManagementScreen
import com.example.theoraclesplate.ui.admin.pendingsellers.presentation.PendingSellersScreen
import com.example.theoraclesplate.ui.auth.presentation.LoginScreen
import com.example.theoraclesplate.ui.auth.presentation.SignUpScreen
import com.example.theoraclesplate.ui.auth.presentation.AdminLoginScreen
import com.example.theoraclesplate.ui.auth.presentation.SellerLoginScreen
import com.example.theoraclesplate.ui.auth.presentation.SellerSignupScreen
import com.example.theoraclesplate.ui.auth.presentation.DeliveryLoginScreen
import com.example.theoraclesplate.ui.auth.presentation.DeliverySignupScreen
import com.example.theoraclesplate.ui.cart.presentation.CartScreen
import com.example.theoraclesplate.ui.checkout.presentation.CheckoutScreen
import com.example.theoraclesplate.ui.delivery.presentation.DeliveryDashboardScreen
import com.example.theoraclesplate.ui.delivery.presentation.DeliveryProfileScreen
import com.example.theoraclesplate.ui.details.presentation.DetailsScreen
import com.example.theoraclesplate.ui.history.presentation.HistoryScreen
import com.example.theoraclesplate.ui.main.presentation.MainScreen
import com.example.theoraclesplate.ui.profile.presentation.EditProfileScreen
import com.example.theoraclesplate.ui.profile.presentation.ProfileScreen
import com.example.theoraclesplate.ui.seller.presentation.SellerDashboardScreen
import com.example.theoraclesplate.ui.seller.presentation.SellerProfileScreen
import com.example.theoraclesplate.ui.seller.menu.presentation.AddMenuItemScreen
import com.example.theoraclesplate.ui.seller.menu.presentation.EditMenuItemScreen
import com.example.theoraclesplate.ui.splash.presentation.SplashScreen
import com.example.theoraclesplate.ui.start.presentation.StartScreen
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun MainApp() {
    THEORACLESPLATETheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "start") {
            composable("splash") { SplashScreen(navController) }
            composable("start") { StartScreen(navController) }
            composable("login_screen") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("seller_login") { SellerLoginScreen(navController) }
            composable("seller_signup") { SellerSignupScreen(navController) }
            composable("seller_profile") { SellerProfileScreen(navController) }
            composable("delivery_login") { DeliveryLoginScreen(navController) }
            composable("delivery_signup") { DeliverySignupScreen(navController) }
            composable("delivery_profile") { DeliveryProfileScreen(navController) }
            composable("home") { MainScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController) }
            composable("cart_screen") { CartScreen(navController) }
            composable("checkout_screen") { CheckoutScreen(navController) }
            composable("seller_dashboard") { SellerDashboardScreen(navController) }
            composable("add_menu_item") { AddMenuItemScreen(navController) }
            composable("admin_panel") { AdminPanelScreen(navController) }
            composable("delivery_dashboard") { DeliveryDashboardScreen(navController) }
            composable("admin_login") { AdminLoginScreen(navController) }
            composable("admin_dashboard") { AdminDashboardScreen(navController) }

            // Admin Dashboard Screens
            composable("pending_sellers") { PendingSellersScreen() }
            composable("all_users") { AllUsersScreen() }
            composable("all_menu_items") { AllMenuItemsScreen() }
            composable("all_orders") { AllOrdersScreen() }
            composable("analytics") { AnalyticsScreen() }
            composable("delivery_management") { DeliveryManagementScreen() }

            composable(
                route = "details/{foodItemId}/{sellerId}",
                arguments = listOf(
                    navArgument("foodItemId") { type = NavType.StringType },
                    navArgument("sellerId") { type = NavType.StringType },
                )
            ) { 
                DetailsScreen(onBack = { navController.popBackStack() })
            }
            
            composable(
                route = "edit_menu_item/{menuItemId}",
                arguments = listOf(navArgument("menuItemId") { type = NavType.StringType })
            ) {
                val menuItemId = it.arguments?.getString("menuItemId") ?: ""
                EditMenuItemScreen(navController = navController, menuItemId = menuItemId)
            }
        }
    }
}
