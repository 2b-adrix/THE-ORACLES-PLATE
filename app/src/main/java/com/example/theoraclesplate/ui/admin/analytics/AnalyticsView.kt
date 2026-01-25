package com.example.theoraclesplate.ui.admin.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AnalyticsView(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Analytics", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Total Revenue: ${state.analyticsData["totalRevenue"]}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Total Orders: ${state.analyticsData["totalOrders"]}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Total Users: ${state.analyticsData["totalUsers"]}")
        }
    }
}
