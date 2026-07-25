package com.example.theoraclesplate.ui.admin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.ui.auth.viewmodel.AdminAuthEvent
import com.example.theoraclesplate.ui.auth.viewmodel.AdminAuthViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun AdminDashboardScreen(navController: NavController, viewModel: AdminAuthViewModel = hiltViewModel()) {
    AdminDashboardScreenContent(
        onNavigate = { route -> navController.navigate(route) },
        onLogout = {
            viewModel.onEvent(AdminAuthEvent.Logout)
            navController.navigate("start") {
                popUpTo("admin_dashboard") { inclusive = true }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreenContent(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val dashboardItems = listOf(
        DashboardItem("Pending Sellers", Icons.Default.Approval, "pending_sellers"),
        DashboardItem("All Users", Icons.Default.People, "all_users"),
        DashboardItem("All Menu Items", Icons.Default.Fastfood, "all_menu_items"),
        DashboardItem("All Orders", Icons.Default.ShoppingBasket, "all_orders"),
        DashboardItem("Analytics", Icons.Default.Analytics, "analytics"),
        DashboardItem("Delivery Management", Icons.Default.DeliveryDining, "delivery_management")
    )

    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Text(
                    text = "Control Center",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(dashboardItems) { item ->
                        DashboardCard(item = item, onClick = { onNavigate(item.route) })
                    }
                }
            }
        }
    }
}

data class DashboardItem(val title: String, val icon: ImageVector, val route: String)

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    AppCard(
        style = CardStyle.Glass,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun AdminDashboardScreenPreview() {
    THEORACLESPLATETheme {
        AdminDashboardScreenContent(onNavigate = {}, onLogout = {})
    }
}
