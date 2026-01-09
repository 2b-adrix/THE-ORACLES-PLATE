package com.example.theoraclesplate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun MainApp() {
    THEORACLESPLATETheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "admin_login") {
            composable("splash") { SplashScreen(navController) }
            composable("start") { StartScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home") { MainScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController) }
            composable("cart_screen") { CartScreen(navController) }
            composable("checkout_screen") { CheckoutScreen(navController) }
            composable("seller_dashboard") { SellerDashboardScreen(navController) }
            composable("admin_panel") { AdminPanelScreen(navController) }
            composable("delivery_dashboard") { DeliveryDashboardScreen(navController) }
            composable("admin_login") { AdminLoginScreen(navController) }
            composable("admin_dashboard") { AdminDashboardScreen(navController) }
            composable(
                route = "details/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { 
                DetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
