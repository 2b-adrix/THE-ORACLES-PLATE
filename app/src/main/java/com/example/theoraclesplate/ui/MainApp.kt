package com.example.theoraclesplate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.theoraclesplate.ui.admin.AdminDashboardScreen
import com.example.theoraclesplate.ui.admin.AdminPanelScreen
import com.example.theoraclesplate.ui.admin.allmenuitems.AllMenuItemsScreen
import com.example.theoraclesplate.ui.admin.allorders.AllOrdersScreen
import com.example.theoraclesplate.ui.admin.allusers.AllUsersScreen
import com.example.theoraclesplate.ui.admin.analytics.AnalyticsScreen
import com.example.theoraclesplate.ui.admin.deliverymanagement.DeliveryManagementScreen
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersScreen
import com.example.theoraclesplate.ui.auth.LoginScreen
import com.example.theoraclesplate.ui.auth.SignUpScreen
import com.example.theoraclesplate.ui.auth.admin.AdminLoginScreen
import com.example.theoraclesplate.ui.auth.delivery.DeliveryLoginScreen
import com.example.theoraclesplate.ui.auth.delivery.DeliverySignupScreen
import com.example.theoraclesplate.ui.auth.seller.SellerLoginScreen
import com.example.theoraclesplate.ui.auth.seller.SellerSignupScreen
import com.example.theoraclesplate.ui.cart.CartScreen
import com.example.theoraclesplate.ui.checkout.CheckoutScreen
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardScreen
import com.example.theoraclesplate.ui.delivery.DeliveryProfileScreen
import com.example.theoraclesplate.ui.details.DetailsScreen
import com.example.theoraclesplate.ui.history.HistoryScreen
import com.example.theoraclesplate.ui.main.MainScreen
import com.example.theoraclesplate.ui.profile.EditProfileScreen
import com.example.theoraclesplate.ui.profile.ProfileScreen
import com.example.theoraclesplate.ui.seller.SellerDashboardScreen
import com.example.theoraclesplate.ui.seller.SellerProfileScreen
import com.example.theoraclesplate.ui.seller.menu.AddMenuItemScreen
import com.example.theoraclesplate.ui.seller.menu.EditMenuItemScreen
import com.example.theoraclesplate.ui.splash.SplashScreen
import com.example.theoraclesplate.ui.start.StartScreen
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
