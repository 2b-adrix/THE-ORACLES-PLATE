package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun MainScreen(rootNavController: NavController) {
    val bottomNavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        AnimatedCircleBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent, // Make Scaffold transparent
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Black.copy(alpha = 0.8f), // Dark, semi-transparent navbar
                    contentColor = StartColor
                ) {
                    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Search,
                        BottomNavItem.Cart,
                        BottomNavItem.Profile
                    )

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                bottomNavController.navigate(screen.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StartColor,
                                selectedTextColor = StartColor,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = StartColor.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) { 
                    HomeScreen(rootNavController = rootNavController, onViewMenuClick = {
                        bottomNavController.navigate(BottomNavItem.Search.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) 
                }
                composable(BottomNavItem.Search.route) { SearchScreen(rootNavController) }
                composable(BottomNavItem.Cart.route) { CartScreen(rootNavController) }
                composable(BottomNavItem.Profile.route) { ProfileScreen(rootNavController) }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: Int) {
    object Home : BottomNavItem("home_screen", "Home", R.drawable.homeicon)
    object Search : BottomNavItem("search_screen", "Search", R.drawable.searchicon)
    object Cart : BottomNavItem("cart_screen", "Cart", R.drawable.shopicon)
    object Profile : BottomNavItem("profile_screen", "Profile", R.drawable.profile)
}
