package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.ui.admin.analytics.AnalyticsView
import com.example.theoraclesplate.ui.admin.allmenuitems.AllMenuItemsView
import com.example.theoraclesplate.ui.admin.allorders.AllOrdersView
import com.example.theoraclesplate.ui.admin.allusers.AllUsersView
import com.example.theoraclesplate.ui.admin.deliverymanagement.DeliveryManagementView
import com.example.theoraclesplate.ui.admin.notifications.NotificationsView
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersView
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Analytics", "Pending Sellers", "All Users", "All Orders", "All Menu Items", "Delivery Management", "Notifications")
    val auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    navController.navigate("start") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = StartColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = StartColor
                )
            },
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    selectedContentColor = StartColor,
                    unselectedContentColor = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> AnalyticsView()
            1 -> PendingSellersView()
            2 -> AllUsersView()
            3 -> AllOrdersView()
            4 -> AllMenuItemsView()
            5 -> DeliveryManagementView()
            6 -> NotificationsView()
        }
    }
}
