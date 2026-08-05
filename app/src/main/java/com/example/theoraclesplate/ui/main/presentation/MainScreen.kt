package com.example.theoraclesplate.ui.main.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.cart.presentation.CartScreen
import com.example.theoraclesplate.ui.cart.viewmodel.CartViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.home.presentation.HomeScreen
import com.example.theoraclesplate.ui.profile.presentation.ProfileScreen
import com.example.theoraclesplate.ui.search.presentation.SearchScreen
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun MainScreen(
    rootNavController: NavController,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val bottomNavController = rememberNavController()
    val cartState by cartViewModel.cartState.collectAsState()
    val cartCount = cartState.cartItems.sumOf { it.quantity }
    
    MainScreenContent(
        rootNavController = rootNavController,
        bottomNavController = bottomNavController,
        cartCount = cartCount
    )
}

@Composable
fun MainScreenContent(
    rootNavController: NavController,
    bottomNavController: NavHostController,
    cartCount: Int = 0
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 0.dp
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
                            icon = { 
                                BadgedBox(
                                    badge = {
                                        if (screen == BottomNavItem.Cart && cartCount > 0) {
                                            Badge {
                                                Text(text = cartCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(painterResource(id = screen.icon), contentDescription = null)
                                }
                            },
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
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
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

@Preview
@Composable
fun MainScreenPreview() {
    THEORACLESPLATETheme {
        Scaffold(
            containerColor = Color(0xFF1A1A2E),
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.Red,
                    tonalElevation = 0.dp
                ) {
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
                            selected = screen == BottomNavItem.Home,
                            onClick = {},
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Red,
                                selectedTextColor = Color.Red,
                                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                indicatorColor = Color.Red.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        ) {
            Box(Modifier.padding(it).fillMaxSize())
        }
    }
}
