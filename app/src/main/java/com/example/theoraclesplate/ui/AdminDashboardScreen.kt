package com.example.theoraclesplate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminDashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Admin Dashboard", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        DashboardButton(text = "Pending Seller Approvals") { navController.navigate("pending_sellers") }
        DashboardButton(text = "Manage All Users") { navController.navigate("all_users") }
        DashboardButton(text = "View All Menu Items") { navController.navigate("all_menu_items") }
        DashboardButton(text = "View All Orders") { navController.navigate("all_orders") }
        DashboardButton(text = "Analytics") { navController.navigate("analytics") }
        DashboardButton(text = "Delivery Management") { navController.navigate("delivery_management") }
    }
}

@Composable
private fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(50.dp)
    ) {
        Text(text)
    }
}
