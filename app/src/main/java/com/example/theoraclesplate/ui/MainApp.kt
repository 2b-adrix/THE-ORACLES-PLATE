package com.example.theoraclesplate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.theoraclesplate.ui.admin.allmenuitems.AllMenuItemsScreen
import com.example.theoraclesplate.ui.admin.allorders.AllOrdersScreen
import com.example.theoraclesplate.ui.admin.allusers.AllUsersScreen
import com.example.theoraclesplate.ui.admin.analytics.AnalyticsScreen
import com.example.theoraclesplate.ui.admin.deliverymanagement.DeliveryManagementScreen
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersScreen
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun MainApp() {
    THEORACLESPLATETheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "start") {
            composable("splash") { SplashScreen(navController) }
            composable("start") { StartScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("seller_login") { SellerLoginScreen(navController) }
            composable("seller_signup") { SellerSignupScreen(navController) }
            composable("delivery_login") { DeliveryLoginScreen(navController) }
            composable("delivery_signup") { DeliverySignupScreen(navController) }
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
            composable("pending_sellers") { PendingSellersScreen(navController) }
            composable("all_users") { AllUsersScreen(navController) }
            composable("all_menu_items") { AllMenuItemsScreen(navController) }
            composable("all_orders") { AllOrdersScreen(navController) }
            composable("analytics") { AnalyticsScreen(navController) }
            composable("delivery_management") { DeliveryManagementScreen(navController) }

            composable(
                route = "details/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { 
                DetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
