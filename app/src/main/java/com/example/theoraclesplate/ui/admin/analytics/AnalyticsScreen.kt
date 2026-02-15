package com.example.theoraclesplate.ui.admin.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            // Show a loading indicator
        } else if (state.error != null) {
            // Show an error message
        } else {
            Text(text = "Total Users: ${state.analyticsData["totalUsers"]}")
            Text(text = "Total Sellers: ${state.analyticsData["totalSellers"]}")
            Text(text = "Total Orders: ${state.analyticsData["totalOrders"]}")
            Text(text = "Total Revenue: $${state.analyticsData["totalRevenue"]}")
            Text(text = "Pending Sellers: ${state.analyticsData["pendingSellers"]}")
        }
    }
}
