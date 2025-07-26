package com.example.theoraclesplate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun MainApp() {
    THEORACLESPLATETheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("start") { StartScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home") { MainScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable("details/{name}/{price}/{image}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                val price = backStackEntry.arguments?.getString("price")
                val image = backStackEntry.arguments?.getString("image")?.toIntOrNull()
                DetailsScreen(navController, name, price, image) 
            }
        }
    }
}
